/*
 * Copyright © 2003 - 2024 The eFaps Team (-)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package simple.escp.printer;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import simple.escp.SimpleEscp;
import simple.escp.Template;
import simple.escp.json.JsonTemplate;

@Disabled
@Tag("RequirePrinterCategory")
public class SimpleEscpTest {

    @Test
    public void printString() {
        final SimpleEscp simpleEscp = new SimpleEscp("EPSON LX-310 ESC/P");
        simpleEscp.print("printString(): Executing SimpleEscpTest.printString()\n" +
            "printString(): And this this a second line.\n");
    }

    @Test
    public void printStringFromDefaultPrinter() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        simpleEscp.print("printStringFromDefaultPrinter(): Executing SimpleEscpTest.printStringFromDefaultPrinter()\n" +
            "printStringFromDefaultPrinter(): And this this a second line.\n");
    }

    @Test
    public void printTemplateBasedOnMap() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        final String json = "{" +
            "\"placeholder\": [" +
                "\"id\"," +
                "\"nickname\"" +
            "]," +
            "\"template\": [" +
                "\"From  : printTemplateBasedOnMap()\"," +
                "\"ID    : ${id}\"," +
                "\"Name  : Mr. ${nickname}.\"" +
            "]" +
        "}";
        final Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        final Map<String, String> data = new HashMap<>();
        data.put("id", "007");
        data.put("nickname", "The Solid Snake");
        simpleEscp.print(template, data, null);
    }

    @Test
    public void printLineSpacingOneEight() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        final String json = "{" +
            "\"pageFormat\": {" +
                "\"lineSpacing\": \"1/8\"" +
            "}," +
            "\"template\": [" +
                "\"LineSpace 1/8: First Line\"," +
                "\"LineSpace 1/8: Second Line\"," +
                "\"LineSpace 1/8: Third Line\"," +
                "\"LineSpace 1/8: Fourth Line\"" +
            "]" +
        "}";
        final Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void print5Cpi() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        final String json = "{" +
            "\"pageFormat\": {" +
                "\"characterPitch\": \"5\"" +
            "}," +
            "\"template\": [" +
                "\"5 cpi: This is an example text in 5 cpi.\"" +
            "]" +
        "}";
        final Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void print10Cpi() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        final String json = "{" +
            "\"pageFormat\": {" +
                "\"characterPitch\": \"10\"" +
            "}," +
            "\"template\": [" +
                "\"10 cpi: This is an example text in 10 cpi.\"" +
            "]" +
        "}";
        final Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void print12Cpi() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        final String json = "{" +
            "\"pageFormat\": {" +
                "\"characterPitch\": \"12\"" +
            "}," +
            "\"template\": [" +
                "\"12 cpi: This is an example text in 12 cpi.\"" +
            "]" +
        "}";
        final Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void print17Cpi() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        final String json = "{" +
            "\"pageFormat\": {" +
                "\"characterPitch\": \"17\"" +
            "}," +
            "\"template\": [" +
                "\"17 cpi: This is an example text in 17 cpi.\"" +
            "]" +
        "}";
        final Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void print20Cpi() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        final String json = "{" +
            "\"pageFormat\": {" +
                "\"characterPitch\": \"20\"" +
            "}," +
            "\"template\": [" +
                "\"20 cpi: This is an example text in 20 cpi.\"" +
            "]" +
        "}";
        final Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void printPageWidth() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        final String json = "{" +
            "\"pageFormat\": {" +
                "\"pageWidth\": \"10\"" +
            "}," +
            "\"template\": [" +
                "\"Page Width 10: 1234567890.\"," +
                "\"Page Width 10: 1234567890.\"," +
                "\"Page Width 10: 1234567890.\"" +
            "]" +
        "}";
        final Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void printLeftAndRightMargin() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        final String json = "{" +
            "\"pageFormat\": {" +
                "\"pageWidth\": \"70\"," +
                "\"leftMargin\": \"50\"," +
                "\"rightMargin\": \"10\"" +
            "}," +
            "\"template\": [" +
                "\"Margin 70,50,10: 1234567890.\"," +
                "\"Margin 70,50,10: 1234567890.\"," +
                "\"Margin 70,50,10: 1234567890.\"" +
            "]" +
        "}";
        final Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void printInRomanTypeface() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        final String json = "{" +
            "\"pageFormat\": {" +
                "\"typeface\": \"roman\"" +
            "}," +
            "\"template\": [" +
                "\"printInRomanTypeface: This should be in roman typeface.\"" +
            "]" +
        "}";
        final Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void printInSansSerifTypeface() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        final String json = "{" +
            "\"pageFormat\": {" +
                "\"typeface\": \"sans-serif\"" +
            "}," +
            "\"template\": [" +
                "\"printInRomanTypeface: This should be in sans-serif typeface.\"" +
            "]" +
        "}";
        final Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void printBold() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        final String json = "{" +
            "\"template\": [" +
                "\"bold: Normal %{BOLD}bold%{BOLD} normal.\"" +
            "]" +
        "}";
        final Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void printItalic() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        final String json = "{" +
            "\"template\": [" +
                "\"italic: Normal %{ITALIC}italic%{ITALIC} normal.\"" +
            "]" +
        "}";
        final Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void printDoubleStrike() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        final String json = "{" +
            "\"template\": [" +
                "\"strike: Normal %{DOUBLE}double strike%{DOUBLE} normal.\"" +
            "]" +
        "}";
        final Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void printUnderline() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        final String json = "{" +
            "\"template\": [" +
                "\"underline: Normal %{UNDERLINE}part with underline%{UNDERLINE} normal.\"" +
            "]" +
        "}";
        final Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void printSuperscriptAndSubscript() {
        final SimpleEscp simpleEscp = new SimpleEscp();
        final String json = "{" +
            "\"template\": [" +
                "\"superscript: Normal %{SUPER}part with superscript%{SUPER} normal.\"," +
                "\"subscript: Normal %{SUB}part with subscript%{SUB} normal.\"" +
            "]" +
        "}";
        final Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }
}
