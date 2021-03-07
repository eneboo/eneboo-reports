import java.io.*; //include Java's standard Input and Output routines
import java.sql.*;
import java.util.*;
import javax.swing.*;
import java.lang.ClassLoader;
import java.lang.Throwable;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterJob;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.JRPrintPage;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;

import net.sf.jasperreports.export.*;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.export.oasis.*;
import net.sf.jasperreports.engine.export.ooxml.*;

import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import org.apache.commons.codec.digest.DigestUtils;

import org.w3c.dom.Document;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import javax.imageio.ImageIO;


public class enebooreports {
 
public static String ficheroTemp, fileTempCloud, exportFormat;
public static Connection conn;
//public static String driverSQL;
public static String build = "Build " + jrversion.eReports();
public static String versionJR = jrversion.jasper();
public static splash splash = new splash();
                public static void main(String[] args) throws IOException {     
			try {

			    String ficheroTemp;
                String impresora,cloudID;
                Image img;
			    JasperPrint print;
			    Boolean pdf,impDirecta, guardaTemporal, modoCloud;
			    int nCopias, nParametrosJasper;
			    long start;
        		Exporter exporter = new JRPdfExporter();
			    Class.forName(args[0]);
			    InputStream is=enebooreports.class.getClassLoader().getResourceAsStream("/otros/init.jasper");
			    if (is == null)
					{
			    	is=enebooreports.class.getClassLoader().getResourceAsStream("otros/init.jasper");
					}
				
			    JasperReport creditos = (JasperReport) JRLoader.loadObject(is);
			    JasperReport report = creditos;

   			   if (enebooreports.conn != null) //Si existe,cerramos la conexión previa
   			   		{
           			enebooreports.conn.close();
   			   		}
   			   	enebooreports.conn = DriverManager.getConnection(args[1],args[2],args[3]);  
   			   	
   			   	InputStreamReader stream_reader = new InputStreamReader(System.in);
   			   	BufferedReader stdin = new BufferedReader(stream_reader);
   			   	
			    do
                              {
			    			
			    			//Inicializa los valores.
			    			ficheroTemp =""; //Nombre fichero Temporal
			    			guardaTemporal = false; //bool que indica si se borra o no el temp al finalizar de usarlo.
                            modoCloud = false;
                            cloudID = "";
                            pdf = false; // exporta a pdf
                            nCopias = 0; // Número de copias
                            impresora =""; // nombre de impresora
                            impDirecta = false; // impresión directa
                            nParametrosJasper = 0; // Número de parametros que vienen (Pareja Nombre-Valor)
                            exportFormat = "pdf";                           
                             
                            System.out.flush();// empties buffer, before you input text
                            do { //Esto es para limpiar suciedad en windows
                            	ficheroTemp = stdin.readLine();
                            	if (ficheroTemp == null )
                            		{	
                            		System.exit(0);
                            		}
                            	ficheroTemp = ficheroTemp.trim();
                            	
                            } while (ficheroTemp.contains("<") || ficheroTemp.contains(">"));
                            
                            
                            enebooreports.ficheroTemp = ficheroTemp;
							if (!ficheroTemp.equals( "version" )) {
								File file_temp = new File(ficheroTemp);

								if (!file_temp.exists()) {
									System.exit(0);
								}
							}

                            start = System.currentTimeMillis(); /* Para controlar el tiempo */	
                            guardaTemporal = Boolean.parseBoolean(stdin.readLine().trim());
                            pdf = Boolean.parseBoolean(stdin.readLine().trim());
                            nCopias = Integer.parseInt(stdin.readLine().trim());
                            impresora = stdin.readLine().trim();
                            impDirecta = Boolean.parseBoolean(stdin.readLine().trim());
                            
                            if (!impDirecta) 
                            	{
                            	splash.mostrar(); //Si el break anterior no cierra la libreria , mostramos splash.
                            	}
                            
                            nParametrosJasper = Integer.parseInt(stdin.readLine().trim());
                            String[] parametroNombre = new String[nParametrosJasper]; 
                            String[] parametroValor = new String[nParametrosJasper];
                          	for(int i = 0; i < nParametrosJasper; i++ ) 
                          		{
	 							parametroNombre[i] = stdin.readLine().trim();
	 							parametroValor[i] = stdin.readLine().trim();  
								}
                          	
                          	java.util.Map<String, Object> hm = new HashMap<String,Object>(); //INICIALIZO MAPA    				
                          	if (ficheroTemp.equals( "version" )) 
                          		{
                          		report = creditos;
                          		guardaTemporal = true;//Para no intentar borrar luego un fichero que no existe
                          		hm.put("VERSION", enebooreports.build);
                          		hm.put("VERSIONJR",enebooreports.versionJR);
                          		}
                          	else 
                          		{     
                          	       if (ficheroTemp.equals( "Repetir" ))//Solo compilar si no se llama repetir.
                          	       		{
                          	    	   	guardaTemporal = true; //Para no intentar borrar luego un fichero que no existe
                          	       		}
                                   else
                                   		{
                                        report = JasperCompileManager.compileReport(ficheroTemp);
                                   		}

	                             for(int j = 0; j < nParametrosJasper; j++ )
	                            	 if(!parametroValor[j].equals( "") && !parametroNombre[j].equals( ""))
	                            	 	{
	                            		 if (parametroNombre[j].equals("REPORT_LOCALE"))
	 						 				{
	 						 				hm.put(parametroNombre[j],new java.util.Locale(parametroValor[j]));
	 						 				}
	 						 			else
	 						 				{
	 						 				hm.put(parametroNombre[j], parametroValor[j]); //Seteamos Parametros en mapa
	 						 				}
	                            		 if(parametroNombre[j].equals("X2CANVAS")) //Comprobamos si estamos en modo cloud
					      					{
	                            			 cloudID = parametroValor[j];
	                            			 modoCloud = true;
					      					}
	                            		 if(parametroNombre[j].equals("FORMAT"))
	                            		 	{
	                            			 exportFormat = parametroValor[j];
	                            		 	}
	                            		 if(parametroNombre[j].equals("XML_URL"))
	                            		 	{
	                            			 Document document = JRXmlUtils.parse(JRLoader.getLocationInputStream(parametroValor[j]));
	                            			 hm.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
	                            		 	}
					      			
					      			
	                            	 	}
					      }
					
                    hm.put("COPIA", 0);
                          	
                          	
					if (modoCloud)
						{
						pdf = true;
						fileTempCloud = DigestUtils.sha1Hex(String.valueOf(System.currentTimeMillis()));
						impresora = "/downloads/"+ fileTempCloud + ".pdf";//Ruta de la impresora
												
						}	
					
					print = JasperFillManager.fillReport(report, hm, enebooreports.conn); //Rellenamos el report compilado
					//Rellenamos con numCopias
					int sizeJasper = print.getPages().size();   
					
					for(int i = 1; i < nCopias;i++){
						for(int x = 0; x < sizeJasper;x++){
							hm.put("COPIA", i);
							JasperPrint printCopy = JasperFillManager.fillReport(report, hm, enebooreports.conn);
							print.addPage(print.getPages().size(), (JRPrintPage) printCopy.getPages().get(x));
						}
					}
					

					if (impDirecta) 
							{
							splash.ocultar();
							if (!impresionDirecta( impresora, print )) {
								JOptionPane.showMessageDialog(null, "Impresión directa en " + impresora + " sufrió un problema." , "Eneboo Reports", 1);
								ficheroTemp = "version"; //Cierra la librería
								}
							
							}
					else if (pdf) {
								File file = new File(impresora);

								if (file.exists() && !file.delete())
										{
                                     	JOptionPane.showMessageDialog(null, "El fichero previo " + file + " no se puede borrar." , "Eneboo Reports", 1);        								
										} 
							
								FileOutputStream fos = new FileOutputStream(file,true);

					 			switch (exportFormat) {
					 									case "pdf": 							
					 										exporter = new JRPdfExporter();
					 										//exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(fos));
					 										SimplePdfExporterConfiguration configuration_pdf = new SimplePdfExporterConfiguration();
					 										//configuration_pdf.setPermissions(PdfWriter.AllowCopy | PdfWriter.AllowPrinting);
					 										exporter.setConfiguration(configuration_pdf);
					 										exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(fos));
					 										break;
					 									case "html":
					 										exporter = new HtmlExporter();
					 										exporter.setExporterOutput(new SimpleHtmlExporterOutput(fos));
					 										break;
					 									case "xml":
															exporter = new JRXmlExporter();
															exporter.setExporterOutput(new SimpleWriterExporterOutput(fos));
					 										break;
					 									case "csv": // Exporta el informe a CSV
					 										exporter = new JRCsvExporter();
					 										SimpleCsvExporterConfiguration configurationCsv = new SimpleCsvExporterConfiguration();
					 										configurationCsv.setFieldDelimiter(";");
															exporter.setConfiguration(configurationCsv);
															exporter.setExporterOutput(new SimpleWriterExporterOutput(fos));
					 										break;
					 									case "xls":// Exporta el informe a XLS
					 										exporter = new JRXlsExporter();
					 										SimpleXlsReportConfiguration configurationXls = new SimpleXlsReportConfiguration();
					 										configurationXls.setOnePagePerSheet(true);
					 										configurationXls.setDetectCellType(true);
					 										configurationXls.setCollapseRowSpan(false);
					 										configurationXls.setWhitePageBackground(false);
															exporter.setConfiguration(configurationXls);
															exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(fos));
					 										break;
					 									case "xlsx":
															exporter = new JRXlsxExporter();
															exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(fos));
					 										break;
					 									case "odt":
					 										exporter = new JROdtExporter();
															exporter.setExporterOutput(new SimpleWriterExporterOutput(fos));
					 										break;
					 									default:
					 										JOptionPane.showMessageDialog(null, "Formato desconocido" , "Eneboo Reports", 1);							

					 								}

							exporter.setExporterInput(new SimpleExporterInput(print));
							exporter.exportReport();							
								
								int nIntentos = 0;
								
								while (!file.exists() && nIntentos <= 100) {
    									try { 
        									Thread.sleep(100);
        									nIntentos++;
    									    } catch (InterruptedException ie) { /* safe to ignore */ }
								}
							//fos.flush();
							fos.close();

					 			if(modoCloud && file.exists())
					 				{
					 				Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
									StringSelection ss = new StringSelection(fileTempCloud + ".pdf_" + cloudID);
									cb.setContents(ss, ss);
					 				}
					 				else 
									if (!file.exists())
					 						{
											splash.ocultar();
					 						JOptionPane.showMessageDialog(null, "Se ha producido un problema al generar el " + exportFormat + "." , "Eneboo Reports", 1);
											ficheroTemp = "version"; //Cierra la librería
					 						}
								splash.ocultar();					 			

								} //pdf
					else
							{
							splash.ocultar();
							//java.awt.Toolkit.getDefaultToolkit().beep();
							if (!mostrarVisor( print, build))
									{
								    JOptionPane.showMessageDialog(null, "El Visor sufrió un problema." , "Eneboo Reports", 1);
									ficheroTemp = "version"; //Cierra la librería librería											
									}								          	
							}	
					
				if (!guardaTemporal)
						{
                          File ficheroT = new File(ficheroTemp);
                          if (!ficheroT.delete())
                          		{
                                 JOptionPane.showMessageDialog(null, "El fichero Temporal " + ficheroTemp + " no se puede borrar." , "Eneboo Reports", 1);
                          		}
						}
		
 				} while (!ficheroTemp.equals( "version" ));
 			splash.cerrar(); //Eliminamos la instancia del splash	
 			System.exit(0);
			}
			catch (Exception e) {
			 splash.cerrar(); //Eliminamos la instancia del splash
			 crearLogError(e);       		
		       }  
		
	}
	
	
	
	
	
	
	
	
	
	
	public static Boolean mostrarVisor(JasperPrint print, String build) 
									{
							ImageIcon image_icon = new ImageIcon();
							try {
									JasperViewer viewer = new JasperViewer(print, false);
                                    					viewer.setTitle(print.getName() + " - Eneboo Reports"); 
									InputStream stream = enebooreports.class.getClassLoader().getResourceAsStream("/otros/logo32.gif");
									if (stream == null)
										{
										stream = enebooreports.class.getClassLoader().getResourceAsStream("otros/logo32.gif");
										}
									try {
										image_icon = new ImageIcon(ImageIO.read(stream));
	    									} catch(IOException e) {
										//Sin splash
										}

									viewer.setIconImage(image_icon.getImage());

									//PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
									//printRequestAttributeSet.add(new Copies(nCopias)); // *************** Numero de copias
									//SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
									//configuration.setPrintService(job.getPrintService());
									//configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
									//configuration.setPrintServiceAttributeSet(services[selectedService].getAttributes());
									//exporter.setConfiguration(configuration);									


									viewer.setVisible(true);





									try {
										viewer.setAlwaysOnTop(true);
										viewer.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
										viewer.setAlwaysOnTop(false); //Ahora no interesa estar siempre 
										while (viewer.isShowing())
										  	Thread.sleep(500);	//Esperamos a que el visor se cierre
										//viewer.dispose();
										
									 } finally
										{
										//java.awt.Toolkit.getDefaultToolkit().beep();
										//viewer.dispose();
										return true;
										}							   
								    	}
					 catch (Exception e) {  
            JOptionPane.showMessageDialog(null, "mostrarVisor :: Se ha producido un error (Exception) : \n " + e.toString() , "Eneboo Reports", 1);
	    e.printStackTrace();
		crearLogError(e);
		return false;
		       }
		       }  
	
	
				
					
					
	public static Boolean impresionDirecta(String impresora, JasperPrint print) {
	
		try
		{
		JRPrintServiceExporter exporter = new JRPrintServiceExporter();
		//Aqui imprimimos directamente en var impresora...
		//JasperPrint print = JasperFillManager.fillReport( this.class.getResource("/classpath/yourReport.jasper").getPath(), new HashMap(), new yourReportDataSource());
		PrinterJob job = PrinterJob.getPrinterJob();
		/* Create an array of PrintServices */
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		int selectedService = -1;
		String listadoImpresorasDisponibles = "";
		/* Scan found services to see if anyone suits our needs */
		for(int i = 0; i < services.length;i++)
				{
				if(services[i].getName().contains(impresora))
						{
						job.setPrintService(services[i]);
						selectedService = i;
						break;
						}
				listadoImpresorasDisponibles += services[i].getName() + "\n";
				}
		
		if (services.length == 0)
			{
			listadoImpresorasDisponibles = "¡¡ Opppps !! . No se han detectado impresoras en el sistema";
			}
		
		if ( selectedService > -1) 
			{
			
			PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
			//printRequestAttributeSet.add(new Copies(nCopias)); // *************** Numero de copias
			SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
			configuration.setPrintServiceAttributeSet(services[selectedService].getAttributes()); //Asignamos la impresora directa
			configuration.setPrintService(job.getPrintService());
			configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
			configuration.setDisplayPageDialog(false);
			configuration.setDisplayPrintDialog(false);

			ExporterInput inp = new SimpleExporterInput(print);			
			exporter.setExporterInput(inp);			
			exporter.setConfiguration(configuration);
			try {
				exporter.exportReport();
			    } catch (Exception e) {
				JOptionPane.showMessageDialog(null, "impresionDirecta :: El documento no tiene páginas", "Eneboo Reports", 1);
	    			//e.printStackTrace();
	    			crearLogError(e);
				return false;
				}
			exporter = null;
			} else JOptionPane.showMessageDialog(null, "Eneboo Reports :: impresionDirecta :: No existe la impresora especificaca : ( " + impresora + " ).\n\nEspecifique alguna de las siguientes impresoras :\n" + listadoImpresorasDisponibles , "Eneboo Reports", 1);
						
		}catch (Exception e) {  
            JOptionPane.showMessageDialog(null, "impresionDirecta :: Se ha producido un error (Exception) : \n " + e.toString(), "Eneboo Reports", 1);
	    e.printStackTrace();
	    crearLogError(e);
			return false;
		       }  									
		   return true;    
		  }
		  
	public static void crearLogError(Exception error) {
		try
		{
			String cabecera;
			long tiempo = System.currentTimeMillis();
			String ficheroError = enebooreports.ficheroTemp+"_"+ tiempo + "_error.txt";
			
			///Generamos cabecera
			cabecera = "\nEneboo Reports :\n\n* " + enebooreports.build + ".\n* Versión Jasper Reports " + enebooreports.versionJR + ".\n* Nombre fichero " + enebooreports.ficheroTemp + ".\n\n\n* StackTrace Java :\n";  	

	
			
			FileOutputStream fos = new FileOutputStream(ficheroError);
			PrintStream ps = new PrintStream(fos);
			ps.printf(cabecera);
			error.printStackTrace(ps);
			ps.close();  
			JOptionPane.showMessageDialog(null, "Se ha producido un error : \n" + error.toString() + "\n\nConsulte " + ficheroError + " para más información \n ", "Eneboo Reports", 1);

	
	}catch (Exception e) {  
            JOptionPane.showMessageDialog(null, "crearLogError :: Se ha producido un error (Exception) : \n " + e.toString() + "\nError inicial: \n" + error.toString(), "Eneboo Reports", 1);
	    e.printStackTrace();
			     }
	System.exit(1);  		
	}

}
