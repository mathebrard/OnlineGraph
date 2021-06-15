package og;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * 
 * 345 color=blue size=15
 * 
 * 654 color=blue size=15
 * 
 * 345 654 label=just a stupid relation style=dashed directed=true
 * 
 * 
 * 
 * @author lhogie
 *
 */
public class OGLParser {
	public void parse(Reader in) throws IOException {
		var bis = new BufferedReader(in);
		var g = new Graph<>(null);
		
		while (true) {
			var line = bis.readLine();
			
			if (line == null) {
				return;
			}

			if (line.startsWith("clear")) {
				g.clear();
			}else if (line.startsWith("add vertex")) {
				g.addVertex();
			}else if (line.startsWith("set vertex ")) {
//				String 
//				g.addVertex();
			}


		}
	}
}
