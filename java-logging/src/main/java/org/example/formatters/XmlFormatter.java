package org.example.formatters;

import org.example.core.LogLevel;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XmlFormatter implements LogFormatter {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Override
    public String format(String loggerName, LogLevel level, String message, Throwable throwable) {
        try {
            var docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            var doc = docBuilder.newDocument();

            var logEntry = doc.createElement("LogEntry");
            doc.appendChild(logEntry);

            addElement(doc, logEntry, "Timestamp", DATE_FORMAT.format(new Date()));
            addElement(doc, logEntry, "Logger", loggerName);
            addElement(doc, logEntry, "Level", level.name());
            addElement(doc, logEntry, "Message", message);

            if (throwable != null) {
                var exceptionElement = doc.createElement("Exception");
                addElement(doc, exceptionElement, "Type", throwable.getClass().getName());
                addElement(doc, exceptionElement, "Message", throwable.getMessage());
                logEntry.appendChild(exceptionElement);

                var stackTraceElement = doc.createElement("StackTrace");
                for (var element : throwable.getStackTrace()) {
                    addElement(doc, stackTraceElement, "Frame", element.toString());
                }
                exceptionElement.appendChild(stackTraceElement);
            }

            return transformToString(doc);
        } catch (Exception e) {
            return "<Error>Failed to format log as XML: " + e.getMessage() + "</Error>";
        }
    }

    private void addElement(org.w3c.dom.Document doc, org.w3c.dom.Element parent,
                            String name, String value) {
        var element = doc.createElement(name);
        element.appendChild(doc.createTextNode(value));
        parent.appendChild(element);
    }

    private String transformToString(org.w3c.dom.Document doc) throws Exception {
        var transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        var writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }
}