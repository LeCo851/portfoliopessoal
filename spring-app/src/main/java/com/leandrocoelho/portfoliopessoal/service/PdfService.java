package com.leandrocoelho.portfoliopessoal.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class PdfService {

    @Value("classpath:resume.md")
    private Resource resumeResource;

    public byte[] generateResumePdf() throws IOException{

        String markdown = new String(resumeResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);


        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String bodyHtml = renderer.render(parser.parse(markdown));

        String fullHtml = buildHtmlDocument(bodyHtml);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()){

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(fullHtml,null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        }
    }

    private String buildHtmlDocument(String bodyContent){
        return """
            <html>
            <head>
                <style>
                    body {
                        font-family: 'Helvetica', 'Arial', sans-serif;
                        font-size: 12px;
                        line-height: 1.5;
                        color: #333;
                        margin: 40px;
                    }
                    h1 {
                        color: #2563eb; /* Aquele Azul Java do seu site */
                        border-bottom: 2px solid #2563eb;
                        padding-bottom: 10px;
                        margin-top: 0;
                    }
                    h2 {
                        color: #0f172a;
                        margin-top: 20px;
                        border-bottom: 1px solid #ddd;
                        padding-bottom: 5px;
                    }
                    h3 {
                        color: #444;
                        margin-bottom: 5px;
                    }
                    ul {
                        margin-top: 5px;
                    }
                    li {
                        margin-bottom: 5px;
                    }
                    a {
                        color: #2563eb;
                        text-decoration: none;
                    }
                    /* Estilo para código no currículo */
                    code {
                        background-color: #f1f5f9;
                        padding: 2px 4px;
                        border-radius: 4px;
                        font-family: 'Courier New', monospace;
                        font-size: 0.9em;
                    }
                </style>
            </head>
            <body>
                %s
            </body>
            </html>
            """.formatted(bodyContent);
    }
}
