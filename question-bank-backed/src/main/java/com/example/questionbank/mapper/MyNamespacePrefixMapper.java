package com.example.questionbank.mapper;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import org.docx4j.jaxb.NamespacePrefixMapperUtils;

public class MyNamespacePrefixMapper extends NamespacePrefixMapper {
    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        return suggestion;
    }
}

