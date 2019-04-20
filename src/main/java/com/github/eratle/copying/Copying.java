package com.github.eratle.copying;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.github.eratle.copying.Preset.IOPath;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Copying extends JFrame implements ActionListener, ListSelectionListener {

	//Gsonインスタンス
	static Gson gson;

	//コンフィグファイル名
	private final String configName = "config.json";

	//ヘルプファイル名
	private final String helpName = "help.html";

	//プリセットコンボボックス
	private JComboBox<String> presetBox;

	//プリセットコンボボックスモデル
	private DefaultComboBoxModel<String> presetModel;

	//プリセットアイテムリスト
	private JList<String> presetItemList;

	//プリセットアイテムリストモデル
	private DefaultListModel<String> presetItemModel;

	//プリセットマップ
	private Map<String, List<IOPath>> presetMap;

	//アイテムリスト編集用パネル
	private JPanel itemSetPanel;

	//リストで選択中のモデルの情報を出力するラベル
	private JLabel selectInfoLabel;

	//全て選択, 全て選択解除ボタン
	private JButton allSelectButton, allCancelSelectButton;

	//コピーボタン
	private JButton copyButton;

	//ヘルプボタン
	private JButton helpButton;

	//コンフィグ再読込ボタン
	private JButton reloadButton;

	public Copying() {
		try {
			setIconImage(ImageIO.read(new File(Thread.currentThread().getContextClassLoader().getResource("icon.png").getPath())));
		} catch (IOException e) {
			e.printStackTrace();
		}

		presetMap = new LinkedHashMap<String, List<IOPath>>();

		presetItemModel = new DefaultListModel<String>();
		presetItemList = new JList<String>(presetItemModel);
		presetItemList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		presetItemList.addListSelectionListener(this);
		Dimension iLSPSize = new Dimension();
		iLSPSize.setSize(200, 150);
		JScrollPane itemListScrollPane = new JScrollPane();
		itemListScrollPane.getViewport().setView(presetItemList);
		itemListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		itemListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		itemListScrollPane.setPreferredSize(iLSPSize);

		presetModel = new DefaultComboBoxModel<String>();
		presetBox = new JComboBox<String>(presetModel);
		presetBox.addActionListener(this);
		JPanel selectPresetPanel = new JPanel();
		selectPresetPanel.add(presetBox);

		allSelectButton = new JButton();
		allSelectButton.setText("全て選択");
		allSelectButton.addActionListener(this);
		allCancelSelectButton = new JButton();
		allCancelSelectButton.setText("全て選択解除");
		allCancelSelectButton.addActionListener(this);
		copyButton = new JButton();
		copyButton.setText("コピー");
		copyButton.addActionListener(this);

		helpButton = new JButton();
		helpButton.setText("?");
		helpButton.addActionListener(this);

		reloadButton = new JButton();
		reloadButton.setText("🔃");
		reloadButton.addActionListener(this);

		selectInfoLabel = new JLabel();

		itemSetPanel = new JPanel();
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.RIGHT);
		itemSetPanel.setLayout(layout);
		itemSetPanel.add(selectInfoLabel);
		itemSetPanel.add(allSelectButton);
		itemSetPanel.add(allCancelSelectButton);
		itemSetPanel.add(copyButton);

		JPanel subPanel1 = new JPanel();
		subPanel1.setLayout(new BorderLayout());
		subPanel1.add(itemSetPanel, BorderLayout.CENTER);
		JPanel subPanel2 = new JPanel();
		subPanel2.add(helpButton);
		subPanel2.add(reloadButton);
		subPanel1.add(subPanel2, BorderLayout.WEST);

		JPanel presetBoxPanel = new JPanel();
		presetBoxPanel.setLayout(new BorderLayout());
		presetBoxPanel.add(new JLabel("プリセット："), BorderLayout.WEST);
		presetBoxPanel.add(presetBox, BorderLayout.CENTER);
		getContentPane().add(presetBoxPanel, BorderLayout.NORTH);
		getContentPane().add(subPanel1, BorderLayout.SOUTH);
		getContentPane().add(itemListScrollPane, BorderLayout.CENTER);

		loadConfig();
	}

	public void loadConfig() {

		log.info("設定の読み込みを開始します");

		File configJson = new File(configName);
		if(!configJson.exists()) {

			List<IOPath> list1 = new ArrayList<Preset.IOPath>();
			IOPath path1 = new IOPath();
			path1.setSource("コピー元１");
			path1.setTo("コピー先１");
			IOPath path2 = new IOPath();
			path2.setSource("コピー元２");
			path2.setTo("コピー先２");
			list1.add(path1);
			list1.add(path2);

			List<IOPath> list2 = new ArrayList<Preset.IOPath>();
			IOPath path3 = new IOPath();
			path3.setSource("コピー元３");
			path3.setTo("コピー先３");
			IOPath path4 = new IOPath();
			path4.setSource("コピー元４");
			path4.setTo("コピー先４");
			list2.add(path3);
			list2.add(path4);
			Preset preset = new Preset();
			preset.presets = new LinkedHashMap<String, List<IOPath>>();
			preset.presets.put("サンプルプリセット１", list1);
			preset.presets.put("サンプルプリセット２", list2);
			String output = gson.toJson(preset);
			try {
				FileUtils.writeStringToFile(configJson, output, "UTF-8");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "コンフィグファイルの生成に失敗しました");
			}
			JOptionPane.showMessageDialog(this, "サンプルコンフィグファイルを生成しました");
		}

		File helpFile = new File(helpName);
		if(!helpFile.exists()) {

			try {

				FileUtils.copyURLToFile(new URL("https://drive.google.com/uc?id=1V6Kud0cO_2fGTeXOGhi6C_iYmy0-SelM"), helpFile);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "ヘルプファイルの作成に失敗しました");
				log.error("ヘルプファイルの作成に失敗しました", e);
			}
		}

		String json = null;
		try {
			json = FileUtils.readFileToString(new File(configName), "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}

		presetMap.clear();
		presetMap.putAll(gson.fromJson(json, Preset.class).presets);

		presetModel.removeAllElements();
		for(String name : presetMap.keySet()) {

			presetModel.addElement(name);
		}
	}

	public static void main(String[] args) {

		log.info("プログラムを開始します");

		//LookAndFeelをWindowsに指定
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			log.error("LookAndFeelの指定に失敗しました", e);
		}

		//Gson初期j化
		gson = new GsonBuilder().setPrettyPrinting().create();

		//フレームの作成
		Copying frame = new Copying();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Copying");
		frame.setSize(640, 240);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getSource() == presetBox) {

			if(presetModel.getSize() == 0) {
				return;
			}

			String select = (String) presetBox.getSelectedItem();

			presetItemModel.removeAllElements();
			List<IOPath> paths = presetMap.get(select);
			for(IOPath path : paths) {

				File file = new File(path.getSource());
				String str;
				if(file.isFile()) {

					str = "ファイル名: " + FilenameUtils.getName(path.getSource());
				}else if(file.isDirectory()) {

					str = "ディレクトリ名: " + path.getSource();
				}else {

					str = path.getSource();
				}
				presetItemModel.addElement(str);
			}

			String select1 = (String) presetBox.getSelectedItem();
			List<IOPath> paths1 = presetMap.get(select1);
			presetItemList.setSelectionInterval(0, paths1.size()-1);
		}else if(arg0.getSource() == allSelectButton) {

			String select = (String) presetBox.getSelectedItem();
			List<IOPath> paths = presetMap.get(select);
			presetItemList.setSelectionInterval(0, paths.size()-1);
		}else if(arg0.getSource() == allCancelSelectButton) {

			presetItemList.clearSelection();
		}else if((arg0.getSource() == copyButton)) {

			log.info("コピーを開始します");

			List<String> values = presetItemList.getSelectedValuesList();
			if(values.isEmpty()) {

				JOptionPane.showMessageDialog(this, "コピーするものが指定されていません");
				return;
			}
			for(IOPath path : presetMap.get(presetBox.getSelectedItem())) {

				String fileName;

				File file = new File(path.getSource());
				if(file.isFile()) {

					fileName = FilenameUtils.getName(path.getSource());
				}else if(file.isDirectory()) {

					fileName = path.getSource();
				}else {

					fileName = path.getSource();
				}
				for(String s : values) {

					if(fileName.equals(getFileNameByText(s))) {

						File sourceFile = new File(path.getSource());
						File toFile = new File(path.getTo());

						try {
							if(toFile.isFile()) {
								if(sourceFile.isFile()) {

									log.info("file to file");
									FileUtils.copyFile(sourceFile, toFile, true);
								}else {

									log.info("directory to file");
									JOptionPane.showMessageDialog(this,
										"ディレクトリをファイルにコピーすることはできません\n"
											+ "コピー元: " + sourceFile.getName()
											+ "コピー先" + toFile.getName());
								}
							}else {

								if(!toFile.exists()) {

									log.info(toFile.getName() + "が存在しない為作成します");
									toFile.mkdirs();
								}

								if(sourceFile.isFile()) {

									log.info("file to directory");
									FileUtils.copyFileToDirectory(sourceFile, toFile, true);
								}else {

									log.info("directory to directory");
									FileUtils.copyDirectory(sourceFile, toFile, true);
								}
							}
						}catch(IOException e) {

							JOptionPane.showMessageDialog(this, "コピー中にエラーが発生しました"
								+ "コピー元: " + sourceFile.getName()
								+ "コピー先" + toFile.getName());
						}
					}
				}
			}
			JOptionPane.showMessageDialog(this, "コピーしました");
		}else if(arg0.getSource() == helpButton) {

			try {
				File file = new File("help.html");
				URL url = file.toURI().toURL();
				HelpDialog dialog = new HelpDialog(this, url);
				dialog.setVisible(true);
			}catch(IOException e) {

			}
		}else if(arg0.getSource() == reloadButton) {

			loadConfig();
			JOptionPane.showMessageDialog(this, "コンフィグを再読込しました");
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

		List<String> values = presetItemList.getSelectedValuesList();
		if(values.size() == 1) {

			String source = null, to = null;
			String fileName;
			for(IOPath path : presetMap.get(presetBox.getSelectedItem())) {

				File file = new File(path.getSource());
				if(file.isFile()) {

					fileName = FilenameUtils.getName(path.getSource());
				}else {

					fileName = path.getSource();
				}
				if(fileName.equals(getFileNameByText(values.get(0))))   {

					source = path.getSource();
					to = path.getTo();
				}
			}

			String str = "<html><body>コピー元: " + source + "<br />" + "コピー先" + to + "</body></html>";
			selectInfoLabel.setText(str);
		}else {

			selectInfoLabel.setText("");
		}
	}

	private String getFileNameByText(String s) {

		if(s.contains("ファイル名: ")) {
			return s.replaceAll("ファイル名: ", "");
		}else if(s.contains("ディレクトリ名: ")) {
			return s.replaceAll("ディレクトリ名: ", "");
		}
		return s;
	}
}
