package com.github.eratle.copying;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;

public class HelpDialog extends JDialog {

	private JScrollPane jScrollPane;
	private JEditorPane html;

	public HelpDialog(Frame parent, URL url) throws IOException {
		super(parent, false);

		makeDialog(url);
	}

	private synchronized void open(URL url) {

		try {
			html.setPage(url);
		} catch (IOException e) {

			JOptionPane.showMessageDialog(
					this, "ヘルプの表示に失敗しました\nCopying.jarがあるディレクトリのhelp.txtを開いてください");
		}
	}

	private void makeDialog(URL url) throws IOException {

		setTitle("ヘルプ");
		setSize(640, 480);
		try {
			setIconImage(ImageIO.read(new File(Thread.currentThread().getContextClassLoader().getResource("helpicon.png").getPath())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setLocationRelativeTo(null);
		html = new JEditorPane();
		html.setContentType("text/html;charset=UTF-8");
		html.setEditable(false);
		html.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {

				if(e.getEventType() == EventType.ACTIVATED) {

					URL newURL = e.getURL();
					open(newURL);
				}
			}
		});

		HTMLDocument doc = (HTMLDocument) html.getDocument();
		doc.putProperty("IgnoreCharsetDirective", true);

		html.setPage(url);

		jScrollPane = new JScrollPane();
		jScrollPane.getViewport().setView(html);
		getContentPane().add(jScrollPane);
	}
}
