package com.example.questionbank.service.impl;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.P;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;

@Service
public class WordService {

    private final SpringTemplateEngine templateEngine;

    public WordService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generateWord(Context context) throws Exception {
        // Process Thymeleaf template
        String html = templateEngine.process("question-bank", context);

        // Create a new Word document
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();

        // Add content to the Word document
        P para = wordMLPackage.getMainDocumentPart().createParagraphOfText(html);
        wordMLPackage.getMainDocumentPart().addObject(para);

        // Save the Word document to a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        wordMLPackage.save(outputStream);

        return outputStream.toByteArray();
    }
}
