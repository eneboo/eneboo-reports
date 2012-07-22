eneboo-reports
==============
--
Despósito de reports en formato .jrxml , para la comunidad eneboo.El formato , es el usado por la librería enebooreports.jar

Formato:
i_nombrereport\i_nombrereport.jrxml

Codificación:
Los ficheros tienen que estar guardados en codificación UTF-8.

Carga Xpm:
Dentro de ireports Al diseñar la query -- relacionamos el campo de la
imaggen con fllarge refrefkey.Creamos imagen -->propiedades

Expression class =  java.awt.Image

Image Expression = Xpm.XpmToImage($VARIABLE)
