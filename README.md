## AppArcgis
Desktop application that displays a basemap using the ArcGIS Java SDK to create routes and save them to a routes logger.
## Instruction
Eclipse

Open Eclipse and select File > Import.

In the import wizard, choose Gradle > Existing Gradle Project, then click Next.

Select the java-gradle-starter-project directory as the project root directory.

Click Finish to complete the import.

Select Project > Properties . In Java Build Path, ensure that under the Libraries tab, Modulepath is set to JRE System Library (JavaSE-11). In Java Compiler, ensure that the Use compliance from execution environment 'JavaSE-11' on the 'Java Build Path' checkbox is selected.

Right-click the project in the Project Explorer or Package Explorer and choose Gradle > Refresh Gradle project.

Open the Gradle Tasks view with Window > Show View > Other... > Gradle > Gradle Tasks.

In the Gradle Tasks view, double-click copyNatives under java-gradle-starter-project > build. This will unpack the native library dependencies to $USER_HOME/.arcgis.

In the Gradle Tasks view, double-click run under java-gradle-starter-project > application to run the app.
