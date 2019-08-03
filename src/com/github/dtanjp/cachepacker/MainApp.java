package com.github.dtanjp.cachepacker;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;

/**
 * MainApp.java
 * Description: The main launcher class
 * that initializes most of the GUI components
 * 
 * @author David Tan
 **/
public class MainApp extends JPanel {

	/** Serial version UID **/
	private static final long serialVersionUID = -583323813107758653L;

	/** Constructor **/
	private MainApp() {
		setLayout(null);
		setSize(1000, 800);
		setFocusable(true);
		requestFocusInWindow();
		
		window = new JFrame(Config.getTitle()+" - v"+Config.getVersion());
		window.setResizable(false);
		window.setLayout(null);
		window.setPreferredSize(new Dimension(1000, 800));
		window.setSize(1000, 800);
		window.setContentPane(this);
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocation((kit.getScreenSize().width/2)-500, (kit.getScreenSize().height/2)-400);
		
		display = new DisplayPanel();
		add(display);
		
		label_cachePath = new JLabel("Cache: ");
		label_cachePath.setBounds(30, 10, 500, 25);
		add(label_cachePath);
		
		label_cacheSize = new JLabel("Cache size: ");
		label_cacheSize.setBounds(560, 10, 500, 25);
		add(label_cacheSize);
		
		label_selectedFile = new JLabel("Selected file: ");
		label_selectedFile.setBounds(560, 535, 500, 25);
		add(label_selectedFile);
		
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		directoryChooser = new JFileChooser();
		directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		list = new DefaultListModel<String>();
		
		list_cache = new JList<String>(list);
		list_cache.setBounds(560, 30, 400, 500);
		list_cache.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		list_cache.setAutoscrolls(true);
		list_cache.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        if(evt.getClickCount() == 1) {
		            btn_removeFile.setEnabled(true);
		        }
		        if (evt.getClickCount() == 2) {
		        	String name = list_cache.getSelectedValue();
		        	String filename = name.toLowerCase();
	        		String text = "";
		        	if(filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".gif")) {
		        		display.setImage(cache.get(name));
		        		display.text.setVisible(false);
		        		display.scroll.setVisible(false);
		        	} else {
		        		display.text.setVisible(true);
		        		display.scroll.setVisible(true);
		        		text = new String(cache.get(name));
		        		display.text.setText(text);
		        	}
		            label_selectedFile.setText("Selected file: "+name);
		        }
		    }
		});
		add(list_cache);
		
		scroll = new JScrollPane(list_cache, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setAutoscrolls(true);
		scroll.setWheelScrollingEnabled(true);
		scroll.setBounds(560, 30, 400, 500);
		add(scroll);
		
		btn_files = new JButton("Add files");
		btn_files.setEnabled(false);
		btn_files.setBounds(30, 560, 100, 25);
		btn_files.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int retval = fileChooser.showOpenDialog(instance);
				if(retval == JFileChooser.OPEN_DIALOG) {
					File file = fileChooser.getSelectedFile();
					if(file == null || cache == null || !cache.exist()) return;
					if(!file.exists()) return;
					if(!file.isFile()) return;
					cache.registerFile(file.getAbsolutePath());
					list.addElement(file.getName());
					progressBar.setMaximum(cache.getTotalFiles());
				}
			}
			
		});
		add(btn_files);
		
		btn_cache = new JButton("Load cache");
		btn_cache.setBounds(150, 560, 100, 25);
		btn_cache.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int retval = fileChooser.showOpenDialog(instance);
				if(retval == JFileChooser.OPEN_DIALOG) {
					File file = fileChooser.getSelectedFile();
					if(file == null) return;
					if(!file.exists()) return;
					if(!file.isFile() || !file.getAbsolutePath().endsWith(".cache")) return;
					cache = new Cache(file.getAbsolutePath());
					cache.load();
					list.removeAllElements();
					display.setImage(null);
					for(String s : cache.fileNames())
						list.addElement(s);
				}
			}
			
		});
		add(btn_cache);
		
		btn_createCache = new JButton("Create cache");
		btn_createCache.setBounds(270, 560, 120, 25);
		btn_createCache.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int retval = fileChooser.showSaveDialog(instance);
			    if (retval == JFileChooser.APPROVE_OPTION) {
			      File file = fileChooser.getSelectedFile();
			      if (file == null) return;
			      if (!file.getName().toLowerCase().endsWith(".cache")) {
			        file = new File(file.getParentFile(), file.getName() + ".cache");
			      	try (BufferedWriter worker = new BufferedWriter(new FileWriter(file, true))){
					} catch (IOException ioexception) {
					}
			      	cache = new Cache(file.getAbsolutePath());
			      	list.removeAllElements();
			      	label_cachePath.setText("Cache: "+file.getAbsolutePath());
			      	label_cacheSize.setText("Cache size: "+cache.getCacheSize());
			      	label_selectedFile.setText("Selected file: ");
			      	display.setImage(null);
			      }
			    }
			}
			
		});
		add(btn_createCache);
		
		btn_packDirectory = new JButton("Pack directory");
		btn_packDirectory.setBounds(140, 605, 120, 25);
		btn_packDirectory.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int retval = directoryChooser.showOpenDialog(instance);
			    if (retval == JFileChooser.APPROVE_OPTION) {
			    	File file = directoryChooser.getSelectedFile();
			    	if (file == null) return;
			    	if(!file.isDirectory()) {
			    		JOptionPane.showMessageDialog(null, "Must pick a directory");
			    		return;
			    	}
			    	File saveCache = new File(file.getAbsolutePath()+"/../"+file.getName()+".cache");
			    	if(saveCache.exists()) {
			    		if(saveCache.isFile()) {
			    			int confirm = JOptionPane.showConfirmDialog(null, "There already exists a .cache file at:\n"+saveCache.getAbsolutePath()
			    			+"\nDo you want to overwrite it?");
			    			if(confirm == JOptionPane.OK_OPTION) {
						    	cache = new Cache(saveCache);
			    			} else
			    				return;
			    		} else
			    			return;
			    	} else
				    	cache = new Cache(saveCache);
			    	list.removeAllElements();
			    	cache.loadDirectory(file);
			    	cache.pack();
			    	for(String s : cache.fileNames())
						list.addElement(s);
			    }
			}
			
		});
		add(btn_packDirectory);
		
		btn_pack = new JButton("Pack cache");
		btn_pack.setBounds(560, 560, 120, 25);
		btn_pack.setEnabled(false);
		btn_pack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cache.pack();
			}
			
		});
		add(btn_pack);
		
		btn_extract = new JButton("Extract");
		btn_extract.setBounds(700, 560, 120, 25);
		btn_extract.setEnabled(false);
		btn_extract.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(cache == null) return;
				int retval = directoryChooser.showSaveDialog(instance);
			    if (retval == JFileChooser.APPROVE_OPTION) {
			    	File file = directoryChooser.getSelectedFile();
			    	if (file == null) return;
			    	String path = file.getAbsoluteFile()+"/"+instance.cache.getCacheFile().getName().replace(".cache", "")+"CacheOutput";
			    	if(!new File(path).isDirectory())
			    		new File(path).mkdir();
  
			    	if(list_cache.getSelectedValue() == null) return;
			    	cache.extract(path, list_cache.getSelectedValue());
			    }
			}
			
		});
		add(btn_extract);
		
		btn_dump = new JButton("Extract all");
		btn_dump.setBounds(840, 560, 120, 25);
		btn_dump.setEnabled(false);
		btn_dump.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(cache == null) return;
				int retval = directoryChooser.showSaveDialog(instance);
			    if (retval == JFileChooser.APPROVE_OPTION) {
			    	File file = directoryChooser.getSelectedFile();
			    	if (file == null) return;
			    	String path = file.getAbsoluteFile()+"/"+instance.cache.getCacheFile().getName().replace(".cache", "")+"CacheOutput";
			    	if(!new File(path).isDirectory())
			    		new File(path).mkdir();
  
			    	if(list_cache.getSelectedValue() == null) return;
			    	for(String s : cache.fileNames())
						cache.extract(path, s);
			    }
			}
			
		});
		add(btn_dump);
		
		btn_removeFile = new JButton("Remove selected");
		btn_removeFile.setBounds(690, 605, 140, 25);
		btn_removeFile.setEnabled(false);
		btn_removeFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = list_cache.getSelectedValue();
				list.removeElement(name);
				cache.unregister(name);
				label_cacheSize.setText("Cache size: "+cache.getCacheSize());
			}
			
		});
		add(btn_removeFile);
		
		radio_preserve = new JRadioButton("Keep image ratio");
		radio_preserve.setBounds(30, 635, 140, 30);
		radio_preserve.doClick();
		radio_preserve.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				radio_stretch.setSelected(false);
				radio_preserve.setSelected(true);
				display.keepRatio = true;
			}
			
		});
		add(radio_preserve);
		
		radio_stretch = new JRadioButton("Fill");
		radio_stretch.setBounds(170, 635, 120, 30);
		radio_stretch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				radio_preserve.setSelected(false);
				radio_stretch.setSelected(true);
				display.keepRatio = false;
			}
			
		});
		add(radio_stretch);
		
		progressBar = new JProgressBar(0, 100);
		progressBar.setBounds(30, 690, 930, 30);
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);
		add(progressBar);
		
		menu_file = new JMenu("File");
		menu_help = new JMenu("Help");
		menu_bar = new JMenuBar();
		menu_bar.add(menu_file);
		menu_bar.add(menu_help);
		
		menuItem_exit = new JMenuItem("Exit");
		menuItem_exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
			
		});
		
		menuItem_help = new JMenuItem("Help");
		menuItem_help.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				display.text.setVisible(true);
				display.scroll.setVisible(true);
				String helpText = "-- Help --\n";
				helpText += "1. Start off by setting your cache. Either by creating a new one or \nloading an existing one.\n";
				helpText += "2. You can add and remove files to the cache, but it won't become \npermanent until you pack the cache.\n";
				helpText += "Packing the cache means to overwrite the current cache with the current\ncache contents in memory.\n\n";
				helpText += "\n\n-- !IMPORTANT! --\n";
				helpText += "Any file changes you make to the cache must be \"packed\" in order to \nupdate the cache file.\n";
				helpText += "\n";
				helpText += "For more information, please visit:\n";
				helpText += "https://github.com/DTanJP";
				display.text.setText(helpText);
			}
			
		});
		
		menuItem_about = new JMenuItem("About");
		menuItem_about.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				display.text.setVisible(true);
				display.scroll.setVisible(true);
				String aboutText = "-- About --\n";
				aboutText += "[ "+Config.getTitle()+" v"+Config.getVersion()+" ]\n";
				aboutText += "by David Tan\n";
				aboutText += "This is a application tool written intended to manage game asset files.\n";
				aboutText += "By allowing you to manage your game assets into 1 cache file.\n";
				aboutText += "For more information, please check out:\n";
				aboutText += "https://github.com/DTanJP\n\n";
				aboutText += "-- Changelog --\n";
				String line = "";
				//Reading the changelog is only available in the jar. Can't read it when ran in the IDE.
				try(BufferedReader read = new BufferedReader(new InputStreamReader(MainApp.class.getResourceAsStream("CHANGELOG.txt")))) {
					while((line = read.readLine()) != null)
						aboutText += line+"\n";
				} catch(Exception ex) {}
				display.text.setText(aboutText);
			}
			
		});
		
		menuItem_credits = new JMenuItem("Credits");
		menuItem_credits.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				display.text.setVisible(true);
				display.scroll.setVisible(true);
				String creditText = "-- Credits --\n";
				creditText += Config.getTitle()+" v"+Config.getVersion()+"\n";
				creditText += "made by "+Config.getAuthor()+"\n";
				creditText += "Github: https://github.com/DTanJP\n";
				creditText += "Email: DTanJP@gmail.com\n";
				display.text.setText(creditText);
			}
			
		});
		
		menuItem_setCache = new JMenuItem("Load cache");
		menuItem_setCache.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int retval = fileChooser.showOpenDialog(instance);
				if(retval == JFileChooser.OPEN_DIALOG) {
					File file = fileChooser.getSelectedFile();
					if(file == null) return;
					if(!file.exists()) return;
					if(!file.isFile() || !file.getAbsolutePath().endsWith(".cache")) return;
					cache = new Cache(file.getAbsolutePath());
					cache.load();
					for(String s : cache.fileNames())
						list.addElement(s);
				}
			}
			
		});
		
		menuItem_createCache = new JMenuItem("New cache");
		menuItem_createCache.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int retval = fileChooser.showSaveDialog(instance);
			    if (retval == JFileChooser.APPROVE_OPTION) {
			      File file = fileChooser.getSelectedFile();
			      if (file == null) return;
			      if (!file.getName().toLowerCase().endsWith(".cache")) {
			        file = new File(file.getParentFile(), file.getName() + ".cache");
			      	try (BufferedWriter worker = new BufferedWriter(new FileWriter(file, true))){
					} catch (IOException ioexception) {
					}
			      	cache = new Cache(file.getAbsolutePath());
			      	progressBar.setMaximum(0);
			      	progressBar.setVisible(false);
			      	list.removeAllElements();
			      	label_cachePath.setText("Cache: "+file.getAbsolutePath());
			      	label_cacheSize.setText("Cache size: "+cache.getCacheSize());
			      	label_selectedFile.setText("Selected file: ");
			      	display.setImage(null);
			      }
			    }
			}
			
		});
		
		menuItem_pack = new JMenuItem("Save cache");
		menuItem_pack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(cache != null)
					cache.pack();
			}
			
		});
		menuItem_pack.setEnabled(false);
		
		menuItem_extract = new JMenuItem("Extract file");
		menuItem_extract.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(cache == null) return;
				int retval = directoryChooser.showSaveDialog(instance);
			    if (retval == JFileChooser.APPROVE_OPTION) {
			    	File file = directoryChooser.getSelectedFile();
			    	if (file == null) return;
			    	String path = file.getAbsoluteFile()+"/"+instance.cache.getCacheFile().getName().replace(".cache", "")+"CacheOutput";
			    	if(!new File(path).isDirectory())
			    		new File(path).mkdir();
  
			    	if(list_cache.getSelectedValue() == null) return;
			    	cache.extract(path, list_cache.getSelectedValue());
			    }
			}
		});
		menuItem_extract.setEnabled(false);
		
		menuItem_dump = new JMenuItem("Dump cache");
		menuItem_dump.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(cache == null) return;
				int retval = directoryChooser.showSaveDialog(instance);
			    if (retval == JFileChooser.APPROVE_OPTION) {
			    	File file = directoryChooser.getSelectedFile();
			    	if (file == null) return;
			    	String path = file.getAbsoluteFile()+"/"+instance.cache.getCacheFile().getName().replace(".cache", "")+"CacheOutput";
			    	if(!new File(path).isDirectory())
			    		new File(path).mkdir();
  
			    	if(list_cache.getSelectedValue() == null) return;
			    	for(String s : cache.fileNames())
						cache.extract(path, s);
			    }
			}
			
		});
		menuItem_dump.setEnabled(false);
		
		menuItem_addFile = new JMenuItem("Add file");
		menuItem_addFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int retval = fileChooser.showOpenDialog(instance);
				if(retval == JFileChooser.OPEN_DIALOG) {
					File file = fileChooser.getSelectedFile();
					if(file == null || cache == null || !cache.exist()) return;
					if(!file.exists()) return;
					if(!file.isFile()) return;
					progressBar.setMaximum(cache.getTotalFiles()+1);
					cache.registerFile(file.getAbsolutePath());
					list.addElement(file.getName());
				}
			}
			
		});
		menuItem_addFile.setEnabled(false);
		
		menu_help.add(menuItem_credits);
		menu_help.add(menuItem_about);
		menu_help.add(menuItem_help);
		
		menu_file.add(menuItem_createCache);
		menu_file.add(menuItem_setCache);
		menu_file.add(menuItem_pack);
		menu_file.add(menuItem_extract);
		menu_file.add(menuItem_dump);
		menu_file.add(menuItem_addFile);
		menu_file.add(menuItem_exit);
		
		window.setJMenuBar(menu_bar);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(cache != null) {
			if(cache.getCacheFile().exists() && cache.getCacheFile().isFile()) {
				btn_files.setEnabled(true);
				btn_pack.setEnabled(true);
				btn_extract.setEnabled(true);
				btn_dump.setEnabled(true);
				label_cachePath.setText("Cache: "+cache.getCacheFile().getAbsolutePath());
				label_cacheSize.setText("Cache size: "+cache.getCacheSize());
				
				menuItem_pack.setEnabled(true);
				menuItem_extract.setEnabled(true);
				menuItem_dump.setEnabled(true);
				menuItem_addFile.setEnabled(true);
				
				if(list_cache.getSelectedValue() == "" || list_cache.getSelectedValue() == null)
					btn_removeFile.setEnabled(false);
			}
		}
		repaint();
	}
	
	/** Disable the JComponents if there is no cache **/
	public void disableCache() {
		if(cache != null) return;
		btn_files.setEnabled(false);
		btn_pack.setEnabled(false);
		btn_extract.setEnabled(false);
		btn_dump.setEnabled(false);
		btn_removeFile.setEnabled(false);
		label_cachePath.setText("Cache: ");
		label_cacheSize.setText("Cache size: ");
		menuItem_pack.setEnabled(false);
		menuItem_extract.setEnabled(false);
		menuItem_dump.setEnabled(false);
		menuItem_addFile.setEnabled(false);
	}
	
	/** Main method **/
	public static void main(String[] args) {
		System.setProperty("sun.java2d.opengl", "true");
		System.setProperty("sun.java2d.translaccel", "True");
		System.setProperty("sun.java2d.ddforcevram", "True");
		System.setProperty("sun.java2d.xrender", "true");
		instance = new MainApp();
		instance.window.setVisible(true);
	}
	
	/** Instances **/
	public static MainApp instance = null;
	
	//-- The JFrame window --//
	private JFrame window = null;
	
	//-- The image display --//
	private DisplayPanel display = null;
	
	//-- The file chooser --//
	private JFileChooser fileChooser;
	private JFileChooser directoryChooser;
	
	//-- Buttons --//
	private JButton btn_files, btn_cache, btn_createCache, btn_packDirectory;
	private JButton btn_pack, btn_extract, btn_dump, btn_removeFile;
	
	//-- Radio buttons --//
	private JRadioButton radio_preserve, radio_stretch;
	
	//-- Menu --//
	private JMenuBar menu_bar;
	private JMenu menu_file, menu_help;
	private JMenuItem menuItem_exit, menuItem_help, menuItem_about, menuItem_credits;
	private JMenuItem menuItem_setCache, menuItem_createCache, menuItem_pack, menuItem_extract, menuItem_dump, menuItem_addFile;
	
	//-- List --//
	private JList<String> list_cache;
	private DefaultListModel<String> list;
	private JScrollPane scroll;
	
	//-- Labels --//
	private JLabel label_cachePath, label_cacheSize, label_selectedFile;
	
	//-- Progress bar --//
	public JProgressBar progressBar;
	
	private Cache cache = null;
	private Toolkit kit = Toolkit.getDefaultToolkit();
}
