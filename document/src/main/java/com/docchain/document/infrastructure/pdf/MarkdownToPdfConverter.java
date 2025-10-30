package com.docchain.document.infrastructure.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
@Slf4j
public class MarkdownToPdfConverter {

    private final Parser markdownParser;
    private final HtmlRenderer htmlRenderer;

    public MarkdownToPdfConverter() {
        MutableDataSet options = new MutableDataSet();
        this.markdownParser = Parser.builder(options).build();
        this.htmlRenderer = HtmlRenderer.builder(options).build();
    }

    public byte[] convertToPdf(String markdownContent, String documentTitle) {
        try {
            log.info("Converting markdown to PDF for document: {}", documentTitle);

            Node document = markdownParser.parse(markdownContent);

            String bodyHtml = htmlRenderer.render(document);

            String fullHtml = wrapHtmlWithStyles(bodyHtml, documentTitle);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(fullHtml, null);
            builder.toStream(outputStream);
            builder.run();

            log.info("PDF successfully generated for document: {}", documentTitle);
            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("Error converting markdown to PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert markdown to PDF", e);
        }
    }

    /**
     * Envolve o HTML renderizado com estrutura completa e estilos CSS
     */
    private String wrapHtmlWithStyles(String bodyContent, String title) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8"/>
                    <title>%s</title>
                    <style>
                        @page {
                            size: A4;
                            margin: 2.5cm;
                        }
                        
                        body {
                            font-family: 'Georgia', 'Times New Roman', serif;
                            font-size: 12pt;
                            line-height: 1.6;
                            color: #333;
                            text-align: justify;
                        }
                        
                        h1 {
                            font-size: 24pt;
                            font-weight: bold;
                            text-align: center;
                            margin-top: 0;
                            margin-bottom: 1.5cm;
                            color: #1a1a1a;
                            page-break-after: avoid;
                        }
                        
                        h2 {
                            font-size: 18pt;
                            font-weight: bold;
                            margin-top: 1cm;
                            margin-bottom: 0.5cm;
                            color: #2c2c2c;
                            page-break-after: avoid;
                        }
                        
                        h3 {
                            font-size: 14pt;
                            font-weight: bold;
                            margin-top: 0.8cm;
                            margin-bottom: 0.4cm;
                            color: #3c3c3c;
                            page-break-after: avoid;
                        }
                        
                        p {
                            text-indent: 1.5cm;
                            margin-top: 0.3cm;
                            margin-bottom: 0.3cm;
                            orphans: 3;
                            widows: 3;
                        }
                        
                        ul, ol {
                            margin-left: 2cm;
                            margin-top: 0.3cm;
                            margin-bottom: 0.3cm;
                        }
                        
                        li {
                            margin-bottom: 0.2cm;
                        }
                        
                        strong {
                            font-weight: bold;
                            color: #000;
                        }
                        
                        em {
                            font-style: italic;
                        }
                        
                        blockquote {
                            margin-left: 2cm;
                            margin-right: 2cm;
                            padding: 0.5cm;
                            border-left: 3px solid #ccc;
                            background-color: #f9f9f9;
                            font-style: italic;
                        }
                        
                        code {
                            font-family: 'Courier New', monospace;
                            background-color: #f4f4f4;
                            padding: 2px 4px;
                            border-radius: 3px;
                            font-size: 10pt;
                        }
                        
                        pre {
                            background-color: #f4f4f4;
                            padding: 0.5cm;
                            border-radius: 5px;
                            overflow-x: auto;
                            font-family: 'Courier New', monospace;
                            font-size: 10pt;
                            line-height: 1.4;
                        }
                        
                        a {
                            color: #0066cc;
                            text-decoration: none;
                        }
                        
                        hr {
                            border: none;
                            border-top: 1px solid #ccc;
                            margin: 1cm 0;
                        }
                        
                        table {
                            width: 100%%;
                            border-collapse: collapse;
                            margin: 0.5cm 0;
                        }
                        
                        th, td {
                            border: 1px solid #ddd;
                            padding: 0.3cm;
                            text-align: left;
                        }
                        
                        th {
                            background-color: #f0f0f0;
                            font-weight: bold;
                        }
                    </style>
                </head>
                <body>
                    %s
                </body>
                </html>
                """.formatted(title, bodyContent);
    }
}
