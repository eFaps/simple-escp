simple-escp
===========
This is  a clone of  http://jockihendry.github.io/simple-escp


Introduction
------------

simple-escp is a Java library for text mode printing and ESC/P. This library is designed to make it easy to perform dot matrix printing tasks in Java.  Currently it supports only ESC/P 9-pin commands.

To use simple-escp, first define a JSON template.  For example, the following _template.json_ is a report template:

```javascript
{
  "pageFormat": {
    "characterPitch": "17 cpi",
    "leftMargin": 3,
    "pageWidth": 20,
    "pageLength": 15
  },  
  "template": [
    "User Report",
    "===========",
    "ID    : ${id}",
    "Name  : ${name}"
  ]
}
```

In simple-escp, JSON template will be parsed into a `Report`.  For example, the following code will parse the JSON template:
  
```java
JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/template.json").toURI());
Report report = jsonTemplate.parse();
```

Advanced users may create a `Report` directly using source code rather than JSON template.  For example, the JSON template above can be created by using the following code:

```java
PageFormat pageFormat = new PageFormat();
pageFormat.setCharacterPitch("17 cpi");
pageFormat.setLeftMargin(3);
pageFormat.setPageWidth(20);
pageFormat.setPageLength(15);
Report report = new Report(pageFormat, null, null);
report.append(new TextLine("User Report"), false);
report.append(new TextLine("==========="), false);
report.append(new TextLine("ID    : ${id}", false);
report.append(new TextLine("Name  : ${name}", false);
```

A single `Report` can be filled multiple times by using many differents `DataSource` to produce string that can be printed.  simple-esp support two default implementations of `DataSource`: `MapDataSource` for data source in form of `Map` and `BeanDataSource` for data source in form of JavaBean object.  To make it easy, simple-escp also provides a factory class, `DataSources`, to create an appropriate `DataSource` for given object.  For example, the following code shows how to create `MapDataSource`:

```java
Map<String, String> map = new Hashmap<>();
map.put("id", "007");
map.put("nickname", "Jocki Hendry");
DataSource dataSource = DataSources.from(map);
```

To fill a `Report` with a `DataSource`, create an instance of `FillJob` and execute its `fill()` method.

```java
FillJob fillJob = new FillJob(report, dataSource);
String result = fillJob.fill();
```

To print the String to printer, use one of `SimpleEscp`'s method.  For example, the following code will print to default printer:

```java
SimpleEscp simpleEscp = new SimpleEscp();
simpleEscp.print(result);
```

In fact, the filling steps above can be made simpler by calling `SimpleEscp.print(Template, Map)`:

```
SimpleEscp simpleEscp = new SimpleEscp();
JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/template.json").toURI());
simpleEscp.print(template.parse(), map);
```

simple-escp has a Swing preview panel that can be used to display and print a filled template.  For example, in a `JFrame`, the following panel will display the result of filling our template with its data:

```java
File file = Paths.get("/template.json");
Template template = new Template(file);
PrintPreviewPane printPreview = new PrintPreviewPane(template, data);
```

simple-escp also supports sections such as header, footer, and detail.  It also supports creation of table.  For example, the following JSON template contains a table:

```javascript
{
    "pageFormat": {
        "pageWidth": 50,
        "pageLength": 13,
        "usePageLengthFromPrinter": false
    },
    "template": {
        "header": [
            " %{176 R3} Company Name Page %{PAGE_NO}",
            " %{176 R7} ",
            "%{176 R9} Invoice No: ${invoiceNo:10}",
            " %{176 R5} ",
            " %{176 R3} "
        ],
        "detail": [
            {
                "table": "table_source",
                "border": true,
                "columns": [
                    { "source": "row :: right", "width": 4 },
                    { "source": "code", "width": 9 },
                    { "source": "name", "width": 30, "wrap": true },
                    { "source": "qty", "width": 6 }
                ]
            },
            " ",
            " (Signature) (Signature) "
        ]
    }
}
```

The output is:

![preview](https://cloud.githubusercontent.com/assets/3104399/3616034/9505d74e-0dce-11e4-9d0a-2a7e24dfed61.PNG)

Development
-----------

To generate project files for IntelliJ IDEA, open shell or command prompt, `cd` to this project directory and enter the following command:

```
gradlew idea
```

Double-click on _simple-escp.ipr_ to open the project in IntelliJ IDEA.  To be able to display Gradle taks in IntelliJ IDEA, select **File**, **Import Module** and  choose _build.gradle_ from this project.  Available Gradle tasks can be displayed by selecting **View**, **Tool Windows**, **Gradle**.  Click on the **Refresh All Gradle projects** button to synchronize with latest Gradle settings. 

Gradle tasks can also be executed directly in command prompt or shell.  For example, to execute **test** task, `cd` to this project directory and enter the following command:
 
```
gradlew test
```

To run unit test that will print to printer, use the following command:
 
```
gradlew testRequirePrinter 
```
 
To create binary distribution in zip file, use the following command:
 
```
gradlew distZip
```

The output zip file is located at _build/distributions_ directory.

To release this project to bintray, use the following command:

```
gradlew bintrayUpload
```
