package com.bera.swiftbank.service;

import com.bera.swiftbank.config.AppConstants;
import com.bera.swiftbank.dtos.EventMessageDTO;
import com.bera.swiftbank.entity.Transaction;
import com.bera.swiftbank.entity.User;
import com.bera.swiftbank.enums.NotificationType;
import com.bera.swiftbank.repository.TransactionRepo;
import com.bera.swiftbank.repository.UserRepo;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankStatement {
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    UserRepo userRepo;
    private EmailService emailService;
    @Autowired
    KafkaTemplate<String, EventMessageDTO> kafkaTemplate;
    private String FILE_PATH = "C:\\Users\\Hp\\Downloads\\BankStatement.pdf";
    public List<Transaction> generateStatement(String accountNo, String startDate, String endDate) throws FileNotFoundException, DocumentException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        LocalDateTime startDatetime = start.atStartOfDay();
        LocalDateTime enddatetime = end.atTime(23,59,00);

        User user = userRepo.findByAccountNo(accountNo);
        String username = user.getFirtsName();

        List<Transaction> transactionList = transactionRepo.findAll().stream().sorted().limit(10)
                .filter(transaction -> transaction.getAccountNo().equals(accountNo))
                .filter(transaction -> !transaction.getCreatedAt().isBefore(startDatetime) && !transaction.getCreatedAt().isAfter(enddatetime)).collect(Collectors.toList());

        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document= new Document(statementSize);
        OutputStream outputStream = new FileOutputStream(FILE_PATH);
        PdfWriter.getInstance(document,outputStream);
        document.open();

        PdfPTable bankImfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("Bera Banking"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);


        PdfPCell address = new PdfPCell(new Phrase("Bamngalore"));
        address.setBorder(0);
        bankImfoTable.addCell(bankName);
        bankImfoTable.addCell(address);

        PdfPTable statementinfo = new PdfPTable(2);
        PdfPCell customer = new PdfPCell(new Phrase("start date" +startDate));
        customer.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("statement of account"));
        statement.setBorder(0);

        PdfPCell stopdate = new PdfPCell(new Phrase("start date" +endDate));
        stopdate.setBorder(0);
        PdfPCell name = new PdfPCell(new Phrase("customer name " +user.getFirtsName()));
        name.setBorder(0);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        PdfPCell add = new PdfPCell(new Phrase("customer Address"+user.getAddress()));
        add.setBorder(0);

        PdfPTable transactionTable= new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);

        PdfPCell type = new PdfPCell(new Phrase("TYPE"));
        type.setBackgroundColor(BaseColor.BLUE);
        type.setBorder(0);

        PdfPCell amount = new PdfPCell(new Phrase("AMOUNT"));
        amount.setBackgroundColor(BaseColor.BLUE);
        amount.setBorder(0);

        PdfPCell status = new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.BLUE);
        status.setBorder(0);

        transactionTable.addCell(date);
        transactionTable.addCell(type);

        transactionTable.addCell(amount);
        transactionTable.addCell(status);

        transactionList.forEach(transaction -> {
            transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionTable.addCell(new Phrase(transaction.getStatus()));
        });

        statementinfo.addCell(customer);
        statementinfo.addCell(statement);
        statementinfo.addCell(stopdate);
        statementinfo.addCell(name);
        statementinfo.addCell(space);
        statementinfo.addCell(add);

        document.add(bankImfoTable);
        document.add(statementinfo);
        document.add(transactionTable);
        document.close();

        EventMessageDTO mailMessageDTO = new EventMessageDTO(
                NotificationType.MAIL,null,user.getEmail(),
                AppConstants.ACCOUNT_INVOICE_MSG,
                AppConstants.ACCOUNT_INVOICE_BODY,
                "BankInvoice.pdf",new File(FILE_PATH));
        kafkaTemplate.send(AppConstants.TOPIC,mailMessageDTO);
        return transactionList;
    }
}
