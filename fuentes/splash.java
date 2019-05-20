import java.awt.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.io.IOException;

public class splash extends JWindow {
    
    public splash() {
	ImageIcon image_icon = new ImageIcon();
        JPanel content = (JPanel)getContentPane();
        content.setBackground(Color.white);      
        int width = 300;
        int height =300;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width-width)/2;
        int y = (screen.height-height)/2;
        setBounds(x,y,width,height);  
       // JLabel label = new JLabel(new ImageIcon(enebooreports.class.getClass().getResource("/otros/processant.gif")), JLabel.CENTER);
	InputStream stream = getClass().getResourceAsStream("/otros/processant.gif");
	if (stream == null)
		{
		stream = getClass().getResourceAsStream("otros/processant.gif");
		}
	try {
		image_icon = new ImageIcon(ImageIO.read(stream));
	    } catch(IOException e) {
	//Sin splash
	}
        JLabel label = new JLabel(image_icon, JLabel.CENTER);
        content.add(label, BorderLayout.CENTER);
       // Color cBorde = new Color(238, 77, 28,  255);
       // content.setBorder(BorderFactory.createLineBorder(cBorde, 2));
    }
    
    public void mostrar() {
    setVisible(true);
    setAlwaysOnTop(true);
    }
    
    public void ocultar() {
    setVisible(false);
    }
    
    public void cerrar() {
    dispose();
    }

}
