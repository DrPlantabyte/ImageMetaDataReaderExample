

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.swing.JFileChooser;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Code provided "AS IS" without any warrantees or guarantees. You are free to 
 * use however you wish.
 * @author CCHall
 */
public class ImageMetaDataTester {
	public static void main(String[] p){
		
		File[] files;
		if (p.length <= 0) {
			files = new File[1];
			files[0] = askForFile();
		} else {
			files = new File[p.length];
			for (int i = 0; i < p.length; i++) {
				files[i] = new File(p[i]);
			}
		}
		for (File f : files) {
			try {
				if (f == null) {
					System.exit(0);
				}
				String fileName = f.getName().toLowerCase(Locale.US);
				String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
				System.out.println("Image type = " + extension);
				ImageReader ir = ImageIO.getImageReadersByFormatName(extension).next();
				ir.setInput(ImageIO.createImageInputStream(f), false, false);
				int index = ir.getMinIndex();
				IIOMetadata imageMetadata = ir.getImageMetadata(index);
				String[] xmlFormats = imageMetadata.getMetadataFormatNames();
				for (String xmlFormat : xmlFormats) {
					IIOMetadataNode root = (IIOMetadataNode) imageMetadata.getAsTree(xmlFormat);
					StringWriter xmlStringWriter = new StringWriter();
					StreamResult streamResult = new StreamResult(xmlStringWriter);
					Transformer transformer = TransformerFactory.newInstance().newTransformer();
					transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
					transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "1");
					DOMSource domSource = new DOMSource(root);
					transformer.transform(domSource, streamResult);
					System.out.println(xmlStringWriter);
				}
			} catch (IOException | TransformerException ex) {
				Logger.getLogger(ImageMetaDataTester.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private static File askForFile() {
		JFileChooser jfc = new JFileChooser();
		if(jfc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) return null;
		return jfc.getSelectedFile();
	}
}
