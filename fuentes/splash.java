import java.awt.*;
import javax.swing.*;

public class splash extends JWindow {
    
    public splash() {
        JPanel content = (JPanel)getContentPane();
        content.setBackground(Color.white);      
        int width = 300;
        int height =300;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width-width)/2;
        int y = (screen.height-height)/2;
        setBounds(x,y,width,height);  
        JLabel label = new JLabel(new ImageIcon(enebooreports.class.getClass().getResource("/otros/processant.gif")), JLabel.CENTER);
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

}
