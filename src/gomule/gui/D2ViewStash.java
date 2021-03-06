/*******************************************************************************
 *
 * Copyright 2007, 2010 Randall
 *
 * This file is part of gomule.
 *
 * gomule is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * gomule is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * gomlue; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 *
 ******************************************************************************/
package gomule.gui;

import gomule.d2s.*;
import gomule.d2x.*;
import gomule.item.*;
import gomule.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import randall.util.*;

/**
 * @author Marco
 */
public class D2ViewStash extends JInternalFrame implements D2ItemContainer, D2ItemListListener
{
    /**
	 *
	 */
	private static final long serialVersionUID = -5346518067556935604L;
	private D2FileManager iFileManager;
    private D2ItemList    iStash;
    private String        iFileName;
    private String        iStashName;

    private D2StashFilter iStashFilter;
    private D2ItemModel   iItemModel;
    private JTable        iTable;

    private JPanel        iContentPane;

    private JEditorPane     iItemText;

    private JButton 	  iPickup;
    private JButton       iDropOne;
    private JButton       iDropAll;

    // item types
    private JCheckBox     iTypeUnique;
    private JCheckBox     iTypeSet;
    private JCheckBox     iTypeRuneWord;
    private JCheckBox     iTypeRare;
    private JCheckBox     iTypeMagical;
    private JCheckBox     iTypeCrafted;
    private JCheckBox     iTypeOther;

    // item categories
    private JRadioButton  iCatArmor;
    private JRadioButton  iCatWeapons;
    private JRadioButton  iCatSocket;
    private JRadioButton  iCatCharm;
    private JRadioButton  iCatMisc;
    private JRadioButton  iCatAll;

    private RandallPanel  iArmorFilter;
    private ArrayList	  iArmorFilterList;

    private RandallPanel  iWeaponFilter;
    private ArrayList	  iWeaponFilterList;

    private RandallPanel  iSocketFilter;
    private JRadioButton  iCatSocketJewel;
    private JRadioButton  iCatSocketGem;
    private JRadioButton  iCatSocketRune;
    private JRadioButton  iCatSocketAll;

    private RandallPanel  iCharmFilter;
    private JRadioButton  iCatCharmSmall;
    private JRadioButton  iCatCharmLarge;
    private JRadioButton  iCatCharmGrand;
    private JRadioButton  iCatCharmAll;

    private RandallPanel  iMiscFilter;
    private JRadioButton  iCatMiscAmulet;
    private JRadioButton  iCatMiscRing;
    private JRadioButton  iCatMiscEssence;
    private JRadioButton  iCatMiscOther;
    private JRadioButton  iCatMiscAll;

    private RandallPanel  iRequerementFilter;
    private JTextField	  iReqMaxLvl;
    private JTextField	  iReqMaxStr;
    private JTextField	  iReqMaxDex;
	private JButton iDelete;
	private JButton iDeleteDups;
	private JCheckBox iTypeSocketed;
	private RandallPanel iSockFilter;
	private JCheckBox iCatSock1;
	private JCheckBox iCatSock2;
	private JCheckBox iCatSock3;
	private JCheckBox iCatSock4;
	private JCheckBox iCatSock5;
	private JCheckBox iCatSock6;
	private JCheckBox iCatSockAll;

	private JCheckBox iQualEth;
	private JCheckBox iQualNorm;
	private JCheckBox iQualExce;
	private AbstractButton iQualEli;
	private AbstractButton iQualAll;
	private JCheckBox iQualOther;

	private JButton iCusFilter;

    public D2ViewStash(D2FileManager pMainFrame, String pFileName)
    {
        super( pFileName, true, true, false, true);

        addInternalFrameListener(new InternalFrameAdapter()
        {
//            public void internalFrameOpened(InternalFrameEvent e)
//            {
//                System.err.println("internalFrameOpened()");
//                iTable.requestFocus();
//            }
            public void internalFrameClosing(InternalFrameEvent e)
            {
                iFileManager.saveAll();
                closeView();
//                System.gc();
            }
        });


        iFileManager = pMainFrame;
        iFileName = pFileName;
        iStashName = getStashName( iFileName );

        iContentPane = new JPanel();
        iContentPane.setLayout(new BorderLayout());

        iStashFilter = new D2StashFilter();
        iItemModel = new D2ItemModel();
        iTable = new JTable(iItemModel);
        iTable.addKeyListener(new KeyAdapter()
        {
            public void keyReleased(KeyEvent e)
            {
                if ( e.getKeyCode() == KeyEvent.VK_Z )
                {
                    int lNew = iTable.getSelectedRow() + 1;
                    if ( lNew < iTable.getRowCount() )
                    {
                        iTable.setRowSelectionInterval(lNew, lNew);
                    }
                }
                else if ( e.getKeyCode() == KeyEvent.VK_A )
                {
                    int lNew = iTable.getSelectedRow() -1;
                    if ( lNew >= 0 )
                    {
                        iTable.setRowSelectionInterval(lNew, lNew);
                    }
                }
            }
        });

//        Font lFont = iTable.getTableHeader().getFont();
//            iTable.getTableHeader().setFont( new Font(lFont.getName(), lFont.getStyle(), lFont.getSize()-2) );
        iTable.getTableHeader().addMouseListener( new MouseAdapter()
        {
            public void mouseReleased(MouseEvent e)
            {
                if ( e.getSource() instanceof JTableHeader )
                {
                    JTableHeader lHeader = (JTableHeader) e.getSource();
                    int lHeaderCol = lHeader.columnAtPoint(new Point(e.getX(), e.getY()));

                    lHeaderCol = lHeader.getColumnModel().getColumn(lHeaderCol).getModelIndex();

                    if ( lHeaderCol != -1 )
                    {
                        iItemModel.sortCol(lHeaderCol);
                    }
                }
            }
        });

        iTable.setDefaultRenderer(String.class, new D2CellStringRenderer() );
        iTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            iTable.getColumnModel().getColumn(0).setPreferredWidth(140);
        iTable.getColumnModel().getColumn(1).setPreferredWidth(11);
        iTable.getColumnModel().getColumn(2).setPreferredWidth(11);
        iTable.getColumnModel().getColumn(3).setPreferredWidth(15);
        JScrollPane lPane = new JScrollPane(iTable);
        lPane.setPreferredSize(new Dimension(257, 100));

        JSplitPane stashConts = new JSplitPane();
        stashConts.setLeftComponent(lPane);

//        iContentPane.add(lPane, BorderLayout.WEST);

        RandallPanel lButtonPanel = getButtonPanel();
        JPanel lTypePanel = getTypePanel();
        JPanel lQualityPanel = getQualityPanel();
        RandallPanel lCategoryPanel = getCategoryPanel();

        iRequerementFilter = new RandallPanel();
        iReqMaxLvl = new JTextField();
        iReqMaxLvl.getDocument().addDocumentListener(iStashFilter);
        iReqMaxStr = new JTextField();
        iReqMaxStr.getDocument().addDocumentListener(iStashFilter);
        iReqMaxDex = new JTextField();
        iReqMaxDex.getDocument().addDocumentListener(iStashFilter);
        iCusFilter = new JButton("Filter...");

        iCusFilter.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
            	filterPopUp();
            }

			private void filterPopUp() {



				final JFrame filterPanel = new JFrame();
				filterPanel.setTitle("Item Filter");
				filterPanel.setLocation((int)iContentPane.getLocationOnScreen().getX() + 100,(int)iContentPane.getLocationOnScreen().getY() + 100);
				filterPanel.setSize(500,300);
				filterPanel.setVisible(true);
				filterPanel.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				Box hRoot = Box.createHorizontalBox();
				Box vControl = Box.createVerticalBox();
				Box hVal = Box.createHorizontalBox();
				Box hButtons = Box.createHorizontalBox();
				Box hLabel1 = Box.createHorizontalBox();
				Box hLabel2 = Box.createHorizontalBox();
				Box hLabel3 = Box.createHorizontalBox();

				final JTextField fStrIn = new JTextField();
				final JTextField fNumIn = new JTextField();
				final JRadioButton fMin = new JRadioButton("Min");
				final JRadioButton fMax = new JRadioButton("Max");
				JButton fOk = new JButton("Ok");
				if(iItemModel.filterVal == -1337){
					fNumIn.setText("");
				}else{
					fNumIn.setText(iItemModel.filterVal + "");
				}
				fStrIn.setText(iItemModel.filterString);


				if(iItemModel.filterMin){
					fMin.setSelected(true);
					fMax.setSelected(false);
				}else{
					fMax.setSelected(true);
					fMin.setSelected(false);
				}

				fMin.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent arg0) {

						if(fMax.isSelected()){
							fMax.setSelected(false);
						}
						fMin.setSelected(true);
						iItemModel.filterMin = true;
					}


				});

				fMax.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent arg0) {


						if(fMin.isSelected()){
							fMin.setSelected(false);
						}

						fMax.setSelected(true);
						iItemModel.filterMin = false;
					}


				});

				fOk.addActionListener(new ActionListener(){
	            public void actionPerformed(ActionEvent pEvent)
	            {

	            	iItemModel.filterString = fStrIn.getText();
	            	try{
	            		if(fNumIn.getText().equals("")){

	            			iItemModel.filterVal = -1337;

	            		}else{
	            	iItemModel.filterVal = Integer.parseInt(fNumIn.getText());
	            		}

						iItemModel.filterOn = true;
//						iItemModel.filterString = "getting magic";
//						iItemModel.filterVal = 10;

						itemListChanged();


		            	filterPanel.dispose();

	            	}catch(NumberFormatException e){
	            		e.printStackTrace();
	            		iItemModel.filterVal = 0;
	            		fNumIn.setBackground(Color.red);
	            	}



	            }

				});

				JButton fClear = new JButton("Clear");

				fClear.addActionListener(new ActionListener(){
	            public void actionPerformed(ActionEvent pEvent)
	            {

	            	fNumIn.setBackground(Color.white);
					iItemModel.filterOn = false;
					iItemModel.filterString = "";
					iItemModel.filterVal = 0;

	            	fStrIn.setText("");

	            	fNumIn.setText("");

	            	itemListChanged();



	            }

				});

				JButton fCancel = new JButton("Cancel");

				fCancel.addActionListener(new ActionListener(){
	            public void actionPerformed(ActionEvent pEvent)
	            {

	            	filterPanel.dispose();

	            }

				});

				filterPanel.getContentPane().add(hRoot);
//				hRoot.add(Box.createRigidArea(new Dimension(250,0)));


				vControl.add(hLabel3);
				hLabel3.add(new JLabel("The superfantastic finder machine."));
				hLabel3.add(Box.createRigidArea(new Dimension(10,0)));
				vControl.add(Box.createRigidArea(new Dimension(0,50)));
				hLabel1.add(new JLabel("Filter String:"));
				hLabel1.add(Box.createRigidArea(new Dimension(168,0)));
				vControl.add(hLabel1);
				hLabel2.add(new JLabel("Filter Value:"));
				hLabel2.add(Box.createRigidArea(new Dimension(168,0)));
				vControl.add(fStrIn);
				vControl.add(Box.createRigidArea(new Dimension(0,25)));
				vControl.add(hLabel2);
				vControl.add(hVal);
				vControl.add(Box.createRigidArea(new Dimension(0,50)));
				vControl.add(hButtons);
				vControl.add(Box.createRigidArea(new Dimension(0,50)));

				hVal.add(fNumIn);
				hVal.add(fMin);
				hVal.add(fMax);

				hButtons.add(fOk);
				hButtons.add(fClear);
				hButtons.add(fCancel);

				Box lazy = Box.createVerticalBox();

				lazy.add(new JLabel("I'm too lazy to code"));
				lazy.add(new JLabel("what should be here."));

				hRoot.add(lazy);
				hRoot.add(Box.createRigidArea(new Dimension(100, 0)));
				hRoot.add(vControl);
				filterPanel.validate();
			}
        });


        iRequerementFilter.addToPanel(new JLabel("MaxLvl"), 0, 0, 1, RandallPanel.NONE);
        iRequerementFilter.addToPanel(iReqMaxLvl, 1, 0, 1, RandallPanel.HORIZONTAL);
        iRequerementFilter.addToPanel(new JLabel("MaxStr"), 2, 0, 1, RandallPanel.NONE);
        iRequerementFilter.addToPanel(iReqMaxStr, 3, 0, 1, RandallPanel.HORIZONTAL);
        iRequerementFilter.addToPanel(new JLabel("MaxDex"), 4, 0, 1, RandallPanel.NONE);
        iRequerementFilter.addToPanel(iReqMaxDex, 5, 0, 1, RandallPanel.HORIZONTAL);
        iRequerementFilter.addToPanel(iCusFilter, 6, 0, 1, RandallPanel.HORIZONTAL);

        RandallPanel lTopPanel = new RandallPanel();
        lTopPanel.addToPanel(lButtonPanel, 0, 0, 1, RandallPanel.HORIZONTAL);
        lTopPanel.addToPanel(lQualityPanel, 0, 1, 1, RandallPanel.HORIZONTAL);
        lTopPanel.addToPanel(lTypePanel, 0, 2, 1, RandallPanel.HORIZONTAL);
        lTopPanel.addToPanel(lCategoryPanel, 0, 3, 1, RandallPanel.HORIZONTAL);
        lTopPanel.addToPanel(iRequerementFilter, 0, 4, 1, RandallPanel.HORIZONTAL);


        iContentPane.add(lTopPanel, BorderLayout.NORTH);

        JPanel lItemPanel = new JPanel();
        iItemText = new JEditorPane();
        iItemText.setContentType("text/html");
        iItemText.setBackground(Color.black);
        JScrollPane lItemScroll = new JScrollPane(iItemText);
        lItemPanel.setLayout(new BorderLayout());
        lItemPanel.add(lItemScroll, BorderLayout.CENTER);
        lItemPanel.setPreferredSize(new Dimension(250, 100));

//        iContentPane.add(lItemPanel, BorderLayout.CENTER);

        stashConts.setRightComponent(lItemPanel);
        stashConts.setDividerSize(3);
        stashConts.setDividerLocation(257);
        iContentPane.add(stashConts);
        setContentPane(iContentPane);

        pack();
        setSize(514, 500);
        setVisible(true);


        connect();

        if (iTable != null)
        {

        	iTable.addMouseListener(new MouseListener(){

				public void mouseClicked(MouseEvent arg0) {
					if(arg0.getButton() == MouseEvent.BUTTON1 && arg0.getClickCount() == 2){
						pickupSelected();
					}
				}
				public void mouseEntered(MouseEvent arg0) {}
				public void mouseExited(MouseEvent arg0) {}
				public void mousePressed(MouseEvent arg0) {}
				public void mouseReleased(MouseEvent arg0) {}
        	});

            iTable.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener()
                    {
                        public void valueChanged(ListSelectionEvent e)
                        {
                            if (iTable.getSelectedRowCount() == 1)
                            {

                            	String dispStr = iItemModel.getItem(iTable.getSelectedRow()).itemDumpHtml(true).replaceAll("<[/]*html>", "");
                                if(!isStash()){
                                	iItemText.setText("<html><font size=3 face=Dialog><font color = white>Item From: "+(((D2ItemListAll) iStash).getFilename(iItemModel.getItem(iTable.getSelectedRow())))+"</font><br><br>"+dispStr+"</font></html>");
                                }else{
                                	iItemText.setText("<html><font size=3 face=Dialog>"+dispStr+"</font></html>");

                                }
                                iItemText.setCaretPosition(0);
                            }
                            else
                            {
                                iItemText.setText("");
                            }
                        }
                    });
        }
        if ( iTable.getRowCount() > 0 )
        {
            iTable.setRowSelectionInterval(0,0);
        }
    }

    public static String getStashName(String pFileName)
    {
        ArrayList lList = RandallUtil.split(pFileName, File.separator, true);
        return (String) lList.get(lList.size()-1);
    }

    public void activateView()
    {
        toFront();
        requestFocusInWindow();
        iTable.requestFocus();
    }

    public boolean isStash()
    {
        return !"all".equalsIgnoreCase( iFileName );
    }

    public void connect()
    {
        try
        {
            iStash = iFileManager.addItemList(iFileName, this);
            itemListChanged();

            if ( isStash() )
            {
	            iPickup.setEnabled(true);
	            iDropOne.setEnabled(true);
	            iDropAll.setEnabled(true);
	            iDropOne.setVisible(true);
	            iDropAll.setVisible(true);
            }
            else
            {
	            iPickup.setEnabled(true);
	            iDropOne.setEnabled(false);
	            iDropAll.setEnabled(false);
	            iDropOne.setVisible(false);
	            iDropAll.setVisible(false);
            }
            iTable.setModel( iItemModel );
        }
        catch( Exception pEx ){
            disconnect(pEx);
        }

    }

    public void disconnect(Exception pEx)
    {
        if ( iStash != null )
        {
            if ( isStash() )
            {
                iFileManager.removeItemList(iFileName, this);
            }
            else
            {
                ArrayList lList = ((D2ItemListAll) iStash).getAllContainers();
                for ( int i = 0 ; i < lList.size() ; i++ )
                {
                    D2ItemList lItemList = (D2ItemList) lList.get(i);
                    iFileManager.removeItemList(lItemList.getFilename(), this);
                }
            }
        }

        iStash = null;
        iPickup.setEnabled(false);
        iDropOne.setEnabled(false);
        iDropAll.setEnabled(false);
        itemListChanged();
    }

    public boolean isHC()
    {
        return iStash.isHC();
    }

    public boolean isSC()
    {
        return iStash.isSC();
    }

    private RandallPanel getButtonPanel()
    {
        RandallPanel lButtonPanel = new RandallPanel(true);

        iPickup = new JButton("Pickup");
        iDelete = new JButton("Delete");
        iDeleteDups = new JButton("Delete Dupes");
        iPickup.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent){
            	pickupSelected();
            }
        });
        lButtonPanel.addToPanel(iPickup, 0, 0, 1, RandallPanel.HORIZONTAL);

        iDropOne = new JButton("Drop");
        iDropOne.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                ((D2Stash) iStash).addItem(D2ViewClipboard.removeItem());
            }
        });
        lButtonPanel.addToPanel(iDropOne, 1, 0, 1, RandallPanel.HORIZONTAL);

        iDropAll = new JButton("Drop All");
        iDropAll.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                try
                {
                	iStash.ignoreItemListEvents();
	                ArrayList lItemList = D2ViewClipboard.removeAllItems();
	                while (lItemList.size() > 0)
	                {
	                    ((D2Stash) iStash).addItem((D2Item) lItemList.remove(0));
	                }
                }
                finally
                {
                	 iStash.listenItemListEvents();
                }
                itemListChanged();
            }
        });

        iDelete.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
                Vector lItemList = new Vector();

                int lRows[] = iTable.getSelectedRows();

                if (lRows.length > 0)
                {
                    for (int i = 0; i < lRows.length; i++)
                    {
                        lItemList.add(iItemModel.getItem(lRows[i]));
                    }
                    try
                    {
                    	iStash.ignoreItemListEvents();
                        for (int i = 0; i < lItemList.size(); i++)
                        {
                       int check = JOptionPane.showConfirmDialog(null, "Delete " + ((D2Item) lItemList.get(i)).getName() + "?");

						if(check == 0){
                            iStash.removeItem((D2Item) lItemList.get(i));
						}
						}

                    }
                    finally
                    {
                    	 iStash.listenItemListEvents();
                    }
                    itemListChanged();
                }
            }
        });
        lButtonPanel.addToPanel(iDelete, 3, 0, 1, RandallPanel.HORIZONTAL);


        iDeleteDups.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pEvent)
            {
              int check = JOptionPane.showConfirmDialog(null, "WARNING: WILL DELETE ALL DUAL FPS. CONTINUE?");

				if(check != 0){
                  return;
				}

                HashMap lItemList = new HashMap();

                    for (int i = 0; i < iTable.getRowCount(); i++)
                    {
                    	if(iItemModel.getItem(i).getFingerprint() != null && !iItemModel.getItem(i).getFingerprint().equals("")){
                        lItemList.put(iItemModel.getItem(i).getFingerprint(),iItemModel.getItem(i));
                    	}
                    }


                    try
                    {
                    	iStash.ignoreItemListEvents();

                        for (int i = 0; i < iTable.getRowCount(); i++)
                        {
                        	if(iItemModel.getItem(i).getFingerprint() != null && !iItemModel.getItem(i).getFingerprint().equals("")){
                            iStash.removeItem(iItemModel.getItem(i));
                        	}
                        }

                        Iterator it = lItemList.keySet().iterator();
                     while(it.hasNext()){
                        	((D2Stash) iStash).addItem(((D2Item)lItemList.get(it.next())));
                     }

                    }
                    finally
                    {
                    	 iStash.listenItemListEvents();
                    }
                    itemListChanged();

            }
        });
        if(isStash()){
        lButtonPanel.addToPanel(iDeleteDups, 4, 0, 1, RandallPanel.HORIZONTAL);
        }
        return lButtonPanel;
    }

    protected void pickupSelected() {
            Vector lItemList = new Vector();

            int lRows[] = iTable.getSelectedRows();

            if (lRows.length > 0)
            {
                for (int i = 0; i < lRows.length; i++)
                {
                    lItemList.add(iItemModel.getItem(lRows[i]));
                }
                try
                {
                	iStash.ignoreItemListEvents();
                    for (int i = 0; i < lItemList.size(); i++)
                    {
                        iStash.removeItem((D2Item) lItemList.get(i));
                        D2ViewClipboard.addItem((D2Item) lItemList.get(i));
                    }
                }
                finally
                {
                    iStash.listenItemListEvents();
                }
                itemListChanged();
            }
	}

	private RandallPanel getTypePanel()
    {
        RandallPanel lTypePanel = new RandallPanel(true);

        iTypeUnique = new JCheckBox("Uniq");
        iTypeUnique.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeUnique, 0, 0, 1, RandallPanel.HORIZONTAL);
        iTypeSet = new JCheckBox("Set");
        iTypeSet.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeSet, 1, 0, 1, RandallPanel.HORIZONTAL);
        iTypeRuneWord = new JCheckBox("Runeword");
        iTypeRuneWord.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeRuneWord, 2, 0, 1, RandallPanel.HORIZONTAL);
        iTypeRare = new JCheckBox("Rare");
        iTypeRare.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeRare, 3, 0, 1, RandallPanel.HORIZONTAL);
        iTypeMagical = new JCheckBox("Magic");
        iTypeMagical.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeMagical, 4, 0, 1, RandallPanel.HORIZONTAL);
        iTypeCrafted = new JCheckBox("Craft");
        iTypeCrafted.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeCrafted, 5, 0, 1, RandallPanel.HORIZONTAL);
        iTypeSocketed = new JCheckBox("Socketed");
        iTypeSocketed.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeSocketed, 6, 0, 1, RandallPanel.HORIZONTAL);
        iTypeOther = new JCheckBox("Other");
        iTypeOther.addActionListener(iStashFilter);
        lTypePanel.addToPanel(iTypeOther, 7, 0, 1, RandallPanel.HORIZONTAL);

        return lTypePanel;
    }


    private RandallPanel getQualityPanel()
    {
        RandallPanel lQualPanel = new RandallPanel(true);

        iQualNorm = new JCheckBox("Normal");
        iQualNorm.addActionListener(iStashFilter);
        lQualPanel.addToPanel(iQualNorm, 0, 0, 1, RandallPanel.HORIZONTAL);
        iQualExce = new JCheckBox("Exceptional");
        iQualExce.addActionListener(iStashFilter);
        lQualPanel.addToPanel(iQualExce, 1, 0, 1, RandallPanel.HORIZONTAL);
        iQualEli = new JCheckBox("Elite");
        iQualEli.addActionListener(iStashFilter);
        lQualPanel.addToPanel(iQualEli, 2, 0, 1, RandallPanel.HORIZONTAL);
        iQualOther = new JCheckBox("Other");
        iQualOther.addActionListener(iStashFilter);
        lQualPanel.addToPanel(iQualOther, 3, 0, 1, RandallPanel.HORIZONTAL);
        iQualAll = new JCheckBox("All");
        iQualAll.addActionListener(iStashFilter);
        lQualPanel.addToPanel(iQualAll, 4, 0, 1, RandallPanel.HORIZONTAL);

        iQualAll.setSelected(true);

        iQualEth = new JCheckBox("Ethereal");
        iQualEth.addActionListener(iStashFilter);
        lQualPanel.addToPanel(iQualEth, 5, 0, 1, RandallPanel.HORIZONTAL);

        return lQualPanel;
    }


    private RandallPanel getCategoryPanel()
    {
        RandallPanel lCategoryPanel = new RandallPanel();

        RandallPanel lCategories = getCategories();

        lCategoryPanel
                .addToPanel(lCategories, 0, 0, 1, RandallPanel.HORIZONTAL);

        return lCategoryPanel;
    }

    private RandallPanel getCategories()
    {
        ButtonGroup lCatBtnGroup = new ButtonGroup();
        RandallPanel lCategories = new RandallPanel(true);
        int lRow = 0;

        iCatArmor = new JRadioButton("Armor");
        iCatArmor.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatArmor, 0, lRow, 1, 0.7, RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatArmor);

        iCatWeapons = new JRadioButton("Weapon");
        iCatWeapons.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatWeapons, 1, lRow, 1, 0.7, RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatWeapons);

        iCatSocket = new JRadioButton("Socket Filler");
        iCatSocket.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatSocket, 2, lRow, 1,
                RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatSocket);

        iCatCharm = new JRadioButton("Charm");
        iCatCharm.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatCharm, 3, lRow, 1, 0.6, RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatCharm);

        iCatMisc = new JRadioButton("Misc");
        iCatMisc.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatMisc, 4, lRow, 1, 0.5, RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatMisc);

        iCatAll = new JRadioButton("All");
        iCatAll.addActionListener(iStashFilter);
        lCategories.addToPanel(iCatAll, 5, lRow, 1, 0.5, RandallPanel.HORIZONTAL);
        lCatBtnGroup.add(iCatAll);

        iCatAll.setSelected(true);
        lRow++;

        iArmorFilter = getCategoriesArmor();
        iArmorFilter.setVisible(false);
        lCategories.addToPanel(iArmorFilter, 0, lRow++, 5, RandallPanel.HORIZONTAL);

        iWeaponFilter = getCategoriesWeapon();
        iWeaponFilter.setVisible(false);
        lCategories.addToPanel(iWeaponFilter, 0, lRow++, 5, RandallPanel.HORIZONTAL);

        iSocketFilter = getCategoriesSocket();
        iSocketFilter.setVisible(false);
        lCategories.addToPanel(iSocketFilter, 0, lRow++, 5, RandallPanel.HORIZONTAL);

        iCharmFilter = getCategoriesCharm();
        iCharmFilter.setVisible(false);
        lCategories.addToPanel(iCharmFilter, 0, lRow++, 5, RandallPanel.HORIZONTAL);

        iMiscFilter = getCategoriesMisc();
        iMiscFilter.setVisible(false);
        lCategories.addToPanel(iMiscFilter, 0, lRow++, 5, RandallPanel.HORIZONTAL);

        iSockFilter = getCategoriesSock();
        iSockFilter.setVisible(false);
        lCategories.addToPanel(iSockFilter, 0, lRow++, 5, RandallPanel.HORIZONTAL);

        return lCategories;
    }

    private RandallPanel getCategoriesArmor()
    {
        ButtonGroup lCatArmorBtnGroup = new ButtonGroup();
        RandallPanel lCategoriesArmor = new RandallPanel(true);

        iArmorFilterList = new ArrayList();
        ArrayList lArmorFilterList = D2BodyLocations.getArmorFilterList();
        for ( int i = 0 ; i < lArmorFilterList.size() ; i++ )
        {
            D2BodyLocations lArmor = (D2BodyLocations) lArmorFilterList.get(i);
            D2RadioButton lBtn = new D2RadioButton(lArmor);
            lBtn.setForeground(Color.BLUE);
            lBtn.addActionListener(iStashFilter);
            lCategoriesArmor.addToPanel(lBtn, i, 0, 1, RandallPanel.HORIZONTAL);
            lCatArmorBtnGroup.add(lBtn);
            if ( lArmor == D2BodyLocations.BODY_ALL )
            {
            	lBtn.setForeground(Color.BLACK);
                lBtn.setSelected(true);
            }
            iArmorFilterList.add(lBtn);
        }

        return lCategoriesArmor;
    }

    private RandallPanel getCategoriesWeapon()
    {
        ButtonGroup lCatWeaponBtnGroup = new ButtonGroup();
        RandallPanel lCategoriesWeapon = new RandallPanel(true);

        int lCurrentRowNr = 0;
        RandallPanel lCurrentRow = new RandallPanel(true);
        lCategoriesWeapon.addToPanel(lCurrentRow, 0, lCurrentRowNr++, 1, RandallPanel.HORIZONTAL);

        iWeaponFilterList = new ArrayList();
        ArrayList lWeaponFilterList = D2WeaponTypes.getWeaponTypeList();
        for ( int i = 0 ; i < lWeaponFilterList.size() ; i++ )
        {
            if ( lWeaponFilterList.get(i) instanceof D2WeaponTypes )
            {
	            D2WeaponTypes lWeapon = (D2WeaponTypes) lWeaponFilterList.get(i);
	            D2RadioButton lBtn = new D2RadioButton(lWeapon);
	            lBtn.setForeground(Color.BLUE);
	            lBtn.addActionListener(iStashFilter);
	            lCurrentRow.addToPanel(lBtn, i, 0, 1, RandallPanel.HORIZONTAL);
	            lCatWeaponBtnGroup.add(lBtn);
	            if ( lWeapon == D2WeaponTypes.WEAP_ALL )
	            {
	            	lBtn.setForeground(Color.BLACK);
	                lBtn.setSelected(true);
	            }
	            iWeaponFilterList.add(lBtn);
            }
            else
            {
                lCurrentRow = new RandallPanel(true);
                lCategoriesWeapon.addToPanel(lCurrentRow, 0, lCurrentRowNr++, 1, RandallPanel.HORIZONTAL);
            }
        }

        return lCategoriesWeapon;
    }

    private RandallPanel getCategoriesSocket()
    {
        ButtonGroup lCatSocketBtnGroup = new ButtonGroup();
        RandallPanel lCategoriesSocket = new RandallPanel(true);

        iCatSocketJewel = new JRadioButton("Jewel");
        iCatSocketJewel.setForeground(Color.BLUE);
        iCatSocketJewel.addActionListener(iStashFilter);
        lCategoriesSocket.addToPanel(iCatSocketJewel, 0, 0, 1, RandallPanel.HORIZONTAL);
        lCatSocketBtnGroup.add(iCatSocketJewel);

        iCatSocketGem = new JRadioButton("Gem");
        iCatSocketGem.setForeground(Color.BLUE);
        iCatSocketGem.addActionListener(iStashFilter);
        lCategoriesSocket.addToPanel(iCatSocketGem, 1, 0, 1, RandallPanel.HORIZONTAL);
        lCatSocketBtnGroup.add(iCatSocketGem);

        iCatSocketRune = new JRadioButton("Rune");
        iCatSocketRune.setForeground(Color.BLUE);
        iCatSocketRune.addActionListener(iStashFilter);
        lCategoriesSocket.addToPanel(iCatSocketRune, 2, 0, 1, RandallPanel.HORIZONTAL);
        lCatSocketBtnGroup.add(iCatSocketRune);

        iCatSocketAll = new JRadioButton("All");
        iCatSocketAll.addActionListener(iStashFilter);
        lCategoriesSocket.addToPanel(iCatSocketAll, 3, 0, 1, RandallPanel.HORIZONTAL);
        lCatSocketBtnGroup.add(iCatSocketAll);

        iCatSocketAll.setSelected(true);

        return lCategoriesSocket;
    }

    private RandallPanel getCategoriesCharm()
    {
        ButtonGroup lCatCharmBtnGroup = new ButtonGroup();
        RandallPanel lCategoriesCharm = new RandallPanel(true);

        iCatCharmSmall = new JRadioButton("Small");
        iCatCharmSmall.setForeground(Color.BLUE);
        iCatCharmSmall.addActionListener(iStashFilter);
        lCategoriesCharm.addToPanel(iCatCharmSmall, 0, 0, 1, RandallPanel.HORIZONTAL);
        lCatCharmBtnGroup.add(iCatCharmSmall);

        iCatCharmLarge = new JRadioButton("Large");
        iCatCharmLarge.setForeground(Color.BLUE);
        iCatCharmLarge.addActionListener(iStashFilter);
        lCategoriesCharm.addToPanel(iCatCharmLarge, 1, 0, 1, RandallPanel.HORIZONTAL);
        lCatCharmBtnGroup.add(iCatCharmLarge);

        iCatCharmGrand = new JRadioButton("Grand");
        iCatCharmGrand.setForeground(Color.BLUE);
        iCatCharmGrand.addActionListener(iStashFilter);
        lCategoriesCharm.addToPanel(iCatCharmGrand, 2, 0, 1, RandallPanel.HORIZONTAL);
        lCatCharmBtnGroup.add(iCatCharmGrand);

        iCatCharmAll = new JRadioButton("All");
        iCatCharmAll.addActionListener(iStashFilter);
        lCategoriesCharm.addToPanel(iCatCharmAll, 3, 0, 1, RandallPanel.HORIZONTAL);
        lCatCharmBtnGroup.add(iCatCharmAll);

        iCatCharmAll.setSelected(true);

        return lCategoriesCharm;
    }

    private RandallPanel getCategoriesMisc()
    {
        ButtonGroup lCatMiscBtnGroup = new ButtonGroup();
        RandallPanel lCategoriesMisc = new RandallPanel(true);

	// amulet
        iCatMiscAmulet = new JRadioButton("Amulet");
        iCatMiscAmulet.setForeground(Color.BLUE);
        iCatMiscAmulet.addActionListener(iStashFilter);
        lCategoriesMisc.addToPanel(iCatMiscAmulet, 0, 0, 1, RandallPanel.HORIZONTAL);
        lCatMiscBtnGroup.add(iCatMiscAmulet);

	// ring
        iCatMiscRing = new JRadioButton("Ring");
        iCatMiscRing.setForeground(Color.BLUE);
        iCatMiscRing.addActionListener(iStashFilter);
        lCategoriesMisc.addToPanel(iCatMiscRing, 1, 0, 1, RandallPanel.HORIZONTAL);
        lCatMiscBtnGroup.add(iCatMiscRing);

	// essence
	iCatMiscEssence = new JRadioButton("Essence");
	iCatMiscEssence.setForeground(Color.BLUE);
	iCatMiscEssence.addActionListener(iStashFilter);
	lCategoriesMisc.addToPanel(iCatMiscEssence, 2, 0, 1, RandallPanel.HORIZONTAL);
	lCatMiscBtnGroup.add(iCatMiscEssence);

	// other
        iCatMiscOther = new JRadioButton("Other");
        iCatMiscOther.setForeground(Color.BLUE);
        iCatMiscOther.addActionListener(iStashFilter);
        lCategoriesMisc.addToPanel(iCatMiscOther, 3, 0, 1, RandallPanel.HORIZONTAL);
        lCatMiscBtnGroup.add(iCatMiscOther);

	// all
        iCatMiscAll = new JRadioButton("All");
        iCatMiscAll.addActionListener(iStashFilter);
        lCategoriesMisc.addToPanel(iCatMiscAll, 4, 0, 1, RandallPanel.HORIZONTAL);
        lCatMiscBtnGroup.add(iCatMiscAll);

        iCatMiscAll.setSelected(true);

        return lCategoriesMisc;
    }

    private RandallPanel getCategoriesSock()
    {
//        ButtonGroup lCatMiscBtnGroup = new ButtonGroup();
        RandallPanel lCategoriesSock = new RandallPanel(true);



        iCatSock1 = new JCheckBox("1 Sock");
        iCatSock1.setForeground(Color.GREEN.darker().darker());
        iCatSock1.addActionListener(iStashFilter);
        lCategoriesSock.addToPanel(iCatSock1, 0, 0, 1, RandallPanel.HORIZONTAL);
//        lCatMiscBtnGroup.add(iCatSock1);

        iCatSock2 = new JCheckBox("2 Sock");
        iCatSock2.setForeground(Color.GREEN.darker().darker());
        iCatSock2.addActionListener(iStashFilter);
        lCategoriesSock.addToPanel(iCatSock2, 1, 0, 1, RandallPanel.HORIZONTAL);
//        lCatMiscBtnGroup.add(iCatSock2);

        iCatSock3 = new JCheckBox("3 Sock");
        iCatSock3.setForeground(Color.GREEN.darker().darker());
        iCatSock3.addActionListener(iStashFilter);
        lCategoriesSock.addToPanel(iCatSock3, 2, 0, 1, RandallPanel.HORIZONTAL);
//        lCatMiscBtnGroup.add(iCatSock3);

        iCatSock4 = new JCheckBox("4 Sock");
        iCatSock4.setForeground(Color.GREEN.darker().darker());
        iCatSock4.addActionListener(iStashFilter);
        lCategoriesSock.addToPanel(iCatSock4, 3, 0, 1, RandallPanel.HORIZONTAL);
//        lCatMiscBtnGroup.add(iCatSock4);

        iCatSock5 = new JCheckBox("5 Sock");
        iCatSock5.setForeground(Color.GREEN.darker().darker());
        iCatSock5.addActionListener(iStashFilter);
        lCategoriesSock.addToPanel(iCatSock5, 4, 0, 1, RandallPanel.HORIZONTAL);
//        lCatMiscBtnGroup.add(iCatSock5);

        iCatSock6 = new JCheckBox("6 Sock");
        iCatSock6.setForeground(Color.GREEN.darker().darker());
        iCatSock6.addActionListener(iStashFilter);
        lCategoriesSock.addToPanel(iCatSock6, 5, 0, 1, RandallPanel.HORIZONTAL);
//        lCatMiscBtnGroup.add(iCatSock6);

        iCatSockAll = new JCheckBox("All");
        iCatSockAll.addActionListener(iStashFilter);
        lCategoriesSock.addToPanel(iCatSockAll, 6, 0, 1, RandallPanel.HORIZONTAL);
//        lCatMiscBtnGroup.add(iCatSockAll);

        iCatSockAll.setSelected(true);
        return lCategoriesSock;
    }

    public void itemListChanged()
    {

        iItemModel.refreshData();
        String lTitle = iStashName;
        if (iStash == null || iItemModel == null)
        {
            lTitle += " (Disconnected)";
        }
        else
        {
            lTitle += " (" + iItemModel.getRowCount() + "/";
            lTitle += iStash.getNrItems() + ")" + ((iStash.isModified()) ? "*" : "");
            if (iStash.isSC() && iStash.isHC())
            {
                lTitle += " (Unknown)";
            }
            else if (iStash.isSC())
            {
                lTitle += " (SC)";
            }
            else if (iStash.isHC())
            {
                lTitle += " (HC)";
            }
        }
        setTitle(lTitle);
    }

    class D2ItemModel implements TableModel
    {
//        private D2ItemList   iStash;
        private ArrayList iItems;
        private ArrayList iTableModelListeners = new ArrayList();
        private ArrayList iSortList = new ArrayList();

        private final Object HEADER[] = new Object[] {new Object(), new Object(), new Object(), new Object(), new Object()};
		private String filterString = "";
		private int filterVal = -1337;
		private boolean filterOn = false;
		private boolean filterMin = true;

        public D2ItemModel()
        {
//            iStash = pStash;
            iSortList.add(HEADER[0]);
            refreshData();
        }

        public void sortCol(int pHeaderCol)
        {
            iSortList.remove(HEADER[pHeaderCol]);
            iSortList.add(0, HEADER[pHeaderCol]);
            sort();
        }

        public int getInteger(JTextField pTextfield)
        {
            String lText = pTextfield.getText();
            if ( lText != null )
            {
                if ( !lText.trim().equals("") )
                {
                    try
                    {
                        pTextfield.setForeground(Color.black);
                        return Integer.parseInt(lText);
                    }
                    catch ( NumberFormatException pEx )
                    {
                        pTextfield.setForeground(Color.red);
                        // do Nothing
                    }
                }
            }
            return -1;
        }

        public void refreshData()
        {
            int lMaxReqLvl = -1;
            int lMaxReqStr = -1;
            int lMaxReqDex = -1;

            if (iTypeUnique != null)
            {
                lMaxReqLvl = getInteger(iReqMaxLvl);
                lMaxReqStr = getInteger(iReqMaxStr);
                lMaxReqDex = getInteger(iReqMaxDex);
            }

            iItems = new ArrayList();
            if ( iStash != null )
            {
	            ArrayList lList = iStash.getItemList();
	            for (int i = 0; i < lList.size(); i++)
	            {
	                D2Item lItem = (D2Item) lList.get(i);

	                boolean lAdd1 = false;
	                boolean lAdd2 = false;

	                if (iTypeUnique == null)
	                {
	                    // initializing, all filters to default
	                    lAdd1 = true;
	                    lAdd2 = true;
	                }
	                else
	                {
	                    if (iTypeUnique.isSelected() && lItem.isUnique())
	                    {
	                        lAdd1 = true;
	                    }
	                    else if (iTypeSet.isSelected() && lItem.isSet())
	                    {
	                        lAdd1 = true;
	                    }
	                    else if (iTypeRuneWord.isSelected() && lItem.isRuneWord())
	                    {
	                        lAdd1 = true;
	                    }
	                    else if (iTypeRare.isSelected() && lItem.isRare())
	                    {
	                        lAdd1 = true;
	                    }
	                    else if (iTypeMagical.isSelected() && lItem.isMagical())
	                    {
	                        lAdd1 = true;
	                    }
	                    else if (iTypeCrafted.isSelected() && lItem.isCrafted())
	                    {
	                        lAdd1 = true;
	                    }
	                    else if (iTypeOther.isSelected() && !lItem.isUnique()
	                            && !lItem.isSet() && !lItem.isRuneWord()
	                            && !lItem.isRare() && !lItem.isMagical()
	                            && !lItem.isCrafted())
	                    {
	                        lAdd1 = true;
	                    }
	                    else if(iTypeSocketed.isSelected() && lItem.isSocketed() && !iTypeUnique.isSelected()
	                            && !iTypeSet.isSelected()
	                            && !iTypeRuneWord.isSelected()
	                            && !iTypeRare.isSelected()
	                            && !iTypeMagical.isSelected()
	                            && !iTypeCrafted.isSelected()
	                            && !iTypeOther.isSelected()){
	                    	lAdd1 = true;
	                    }
	                    else if (!iTypeUnique.isSelected()
	                            && !iTypeSet.isSelected()
	                            && !iTypeRuneWord.isSelected()
	                            && !iTypeRare.isSelected()
	                            && !iTypeMagical.isSelected()
	                            && !iTypeCrafted.isSelected()
	                            && !iTypeOther.isSelected()
	                            && !iTypeSocketed.isSelected())
	                    {
	                        lAdd1 = true;
	                    }




	                    if(lItem.getItemQuality().equals("normal")){

                    		if(!iQualNorm.isSelected() && !iQualAll.isSelected()){
                    			lAdd1 = false;
                    		}

	                    }else if(lItem.getItemQuality().equals("exceptional")){

                    		if(!iQualExce.isSelected() && !iQualAll.isSelected()){
                    			lAdd1 = false;
                    		}

	                    }else if(lItem.getItemQuality().equals("elite")){


                    		if(!iQualEli.isSelected() && !iQualAll.isSelected()){
                    			lAdd1 = false;
                    		}

	                    }else if(lItem.getItemQuality().equals("none")){


                    		if(!iQualOther.isSelected() && !iQualAll.isSelected()){
                    			lAdd1 = false;
                    		}

	                    }

	                    if(!lItem.isEthereal()){
	                    	if(iQualEth.isSelected()){
	                    		lAdd1 = false;
	                    	}
	                    }



//	                    if(iQualNorm.isSelected()){
//	                    	if(!lItem.getItemQuality().equals("normal")){
//	                    		lAdd1 = false;
//	                    	}
//	                    }else
//	                    if(iQualExce.isSelected()){
//	                    	if(!lItem.getItemQuality().equals("exceptional")){
//	                    		lAdd1 = false;
//	                    	}
//	                    }else
//	                    if(iQualEli.isSelected()){
//	                    	if(!lItem.getItemQuality().equals("elite")){
//	                    		lAdd1 = false;
//	                    	}
//	                    }else
//	                    if(iQualOther.isSelected()){
//	                    	if(!lItem.getItemQuality().equals("none")){
//	                    		lAdd1 = false;
//	                    	}
//	                    }

	                    if(iTypeSocketed.isSelected()){

	                    	switch((int)lItem.getSocketNrTotal()){

	                    	case 0:
	                    		lAdd1 = false;
	                    		break;
	                    	case 1:
	                    		if(!iCatSock1.isSelected() && !iCatSockAll.isSelected()){
	                    			lAdd1 = false;
	                    		}
	                    		break;
	                    	case 2:
	                    		if(!iCatSock2.isSelected() && !iCatSockAll.isSelected()){
	                    			lAdd1 = false;
	                    		}
	                    		break;
	                    	case 3:
	                    		if(!iCatSock3.isSelected() && !iCatSockAll.isSelected()){
	                    			lAdd1 = false;
	                    		}
	                    		break;
	                    	case 4:
	                    		if(!iCatSock4.isSelected() && !iCatSockAll.isSelected()){
	                    			lAdd1 = false;
	                    		}
	                    		break;
	                    	case 5:
	                    		if(!iCatSock5.isSelected() && !iCatSockAll.isSelected()){
	                    			lAdd1 = false;
	                    		}
	                    		break;
	                    	case 6:
	                    		if(!iCatSock6.isSelected() && !iCatSockAll.isSelected()){
	                    			lAdd1 = false;
	                    		}
	                    		break;

	                    	}


	                    }

	                    if (lAdd1)
	                    {
	                        if (iCatAll.isSelected())
	                        {
	                            lAdd2 = true;
	                        }
	                        else if (iCatArmor.isSelected() && lItem.isTypeArmor())
	                        {
	                            D2RadioButton lAll = (D2RadioButton) iArmorFilterList.get(iArmorFilterList.size()-1);
	                            if ( lAll.isSelected() )
	                            {
	                                lAdd2 = true;
	                            }

	                            for ( int j = 0 ; j < iArmorFilterList.size() - 1 ; j++ )
	                            {
	                                D2RadioButton lBtn = (D2RadioButton) iArmorFilterList.get(j);
	                                if ( lBtn.isSelected() && lItem.isBodyLocation( (D2BodyLocations) lBtn.getData() ) )
	                                {
	                                    lAdd2 = true;
	                                }
	                            }
	                        }
	                        else if (iCatWeapons.isSelected()
	                                && lItem.isTypeWeapon())
	                        {
	                            D2RadioButton lAll = (D2RadioButton) iWeaponFilterList.get(iWeaponFilterList.size()-1);
	                            if ( lAll.isSelected() )
	                            {
	                                lAdd2 = true;
	                            }

	                            for ( int j = 0 ; j < iWeaponFilterList.size() - 1 ; j++ )
	                            {
	                                D2RadioButton lBtn = (D2RadioButton) iWeaponFilterList.get(j);
	                                if ( lBtn.isSelected() && lItem.isWeaponType( (D2WeaponTypes) lBtn.getData() ) )
	                                {
	                                    lAdd2 = true;
	                                }
	                            }
	//                            lAdd2 = true;
	                        }
	                        else if (iCatSocket.isSelected()
	                                && lItem.isSocketFiller())
	                        {
	                            if ( iCatSocketAll.isSelected() )
	                            {
	                                lAdd2 = true;
	                            }
	                            else if ( iCatSocketJewel.isSelected() && lItem.isJewel() )
	                            {
	                                lAdd2 = true;
	                            }
	                            else if ( iCatSocketGem.isSelected() && lItem.isGem() )
	                            {
	                                lAdd2 = true;
	                            }
	                            else if ( iCatSocketRune.isSelected() && lItem.isRune() )
	                            {
	                                lAdd2 = true;
	                            }
	                        }
	                        else if (iCatCharm.isSelected() && lItem.isCharm())
	                        {
	                            if ( iCatCharmAll.isSelected() )
	                            {
	                                lAdd2 = true;
	                            }
	                            else if ( iCatCharmSmall.isSelected() && lItem.isCharmSmall() )
	                            {
	                                lAdd2 = true;
	                            }
	                            else if ( iCatCharmLarge.isSelected() && lItem.isCharmLarge() )
	                            {
	                                lAdd2 = true;
	                            }
	                            else if ( iCatCharmGrand.isSelected() && lItem.isCharmGrand() )
	                            {
	                                lAdd2 = true;
	                            }
	                        }
	                        else if (iCatMisc.isSelected() && lItem.isTypeMisc()
	                                && !lItem.isSocketFiller() && !lItem.isCharm())
	                        {
	                            if ( iCatMiscAll.isSelected() )
	                            {
	                                lAdd2 = true;
	                            }
	                            else if ( iCatMiscAmulet.isSelected() && lItem.isBodyLocation(D2BodyLocations.BODY_NECK) )
	                            {
	                                lAdd2 = true;
	                            }
	                            else if ( iCatMiscRing.isSelected() && lItem.isBodyLocation(D2BodyLocations.BODY_LRIN) )
	                            {
	                                lAdd2 = true;
	                            }
	                            else if ( iCatMiscOther.isSelected() && !lItem.isBodyLocation(D2BodyLocations.BODY_NECK) && !lItem.isBodyLocation(D2BodyLocations.BODY_LRIN) )
	                            {
	                                lAdd2 = true;
	                            }
	                        }
	                    }
	                }

	                if ( lAdd1 && lAdd2 )
	                {
						if(filterOn){

							if(!lItem.conforms(filterString, filterVal, filterMin )){
								lAdd1 = false;
							}

	                	}

	                    if ( lMaxReqLvl != -1 )
	                    {
	                        if ( lItem.getReqLvl() > lMaxReqLvl )
	                        {
	                            lAdd1 = false;
	                        }
	                    }
	                    if ( lMaxReqStr != -1 )
	                    {
	                        if ( lItem.getReqStr() > lMaxReqStr )
	                        {
	                            lAdd1 = false;
	                        }
	                    }
	                    if ( lMaxReqDex != -1 )
	                    {
	                        if ( lItem.getReqDex() > lMaxReqDex )
	                        {
	                            lAdd1 = false;
	                        }
	                    }

	                    if ( lAdd1 )
	                    {
	                        iItems.add(lItem);
	                    }
	                }
	            }
            }
            sort();
        }

        public void sort()
        {
            Collections.sort(iItems, new Comparator()
            {
                public int compare(Object pObj1, Object pObj2)
                {
                    D2Item lItem1 = (D2Item) pObj1;
                    D2Item lItem2 = (D2Item) pObj2;

                    for ( int i = 0 ; i < iSortList.size() ; i++ )
                    {
                        Object lSort = iSortList.get(i);

                        if ( lSort ==  HEADER[0] )
                        {
                            return lItem1.getName().compareTo(lItem2.getName());
                        }
                        else if ( lSort ==  HEADER[1] )
                        {
                            return lItem1.getReqLvl() - lItem2.getReqLvl();
                        }
                        else if ( lSort ==  HEADER[2] )
                        {
                            return lItem1.getReqStr() - lItem2.getReqStr();
                        }
                        else if ( lSort ==  HEADER[3] )
                        {
                            return lItem1.getReqDex() - lItem2.getReqDex();
                        }
                        else if ( lSort ==  HEADER[4] )
                        {
                            String lFileName1 = ((D2ItemListAll) iStash).getFilename(lItem1);
                            String lFileName2 = ((D2ItemListAll) iStash).getFilename(lItem2);
                            return lFileName1.compareTo(lFileName2);
                        }
                    }

                    return 0;
                }
            });
            fireTableChanged();
        }

        public int getRowCount()
        {
            return iItems.size();
        }

        public D2Item getItem(int pRow)
        {
            return (D2Item) iItems.get(pRow);
        }

        public int getColumnCount()
        {
                return 4;
        }

        public String getColumnName(int pCol)
        {
            switch (pCol)
            {
            case 0:
                return "Name";
            case 1:
                return "lvl";
            case 2:
                return "str";
            case 3:
                return "dex";
//            case 4:
//                return "Type";
            default:
                return "";
            }
        }

        public Class getColumnClass(int pCol)
        {
            return String.class;
        }

        public boolean isCellEditable(int pRow, int pCol)
        {
            return false;
        }

        public Object getValueAt(int pRow, int pCol)
        {
            D2Item lItem = (D2Item) iItems.get(pRow);
            switch (pCol)
            {
            case 0:
                return new D2CellValue( lItem.getItemName(), lItem, iFileManager.getProject());
            case 1:
                return new D2CellValue( getStringValue(lItem.getReqLvl()), lItem, iFileManager.getProject());
            case 2:
                return new D2CellValue( getStringValue(lItem.getReqStr()), lItem, iFileManager.getProject());
            case 3:
                return new D2CellValue( getStringValue(lItem.getReqDex()), lItem, iFileManager.getProject());
            case 4:
                String lFileName = ((D2ItemListAll) iStash).getFilename(lItem);
                String lType = ( lFileName.toLowerCase().endsWith(".d2s") ) ? "C":"S";
                return new D2CellValue( lType, lFileName, lItem, iFileManager.getProject() );
            default:
                return "";
            }
        }

        private String getStringValue(int pValue)
        {
            if ( pValue == -1 )
            {
                return "";
            }
            return Integer.toString( pValue );
        }

        public void setValueAt(Object pValue, int pRow, int pCol)
        {
            // Do nothing
        }

        public void addTableModelListener(TableModelListener pListener)
        {
            iTableModelListeners.add(pListener);
        }

        public void removeTableModelListener(TableModelListener pListener)
        {
            iTableModelListeners.remove(pListener);
        }

        public void fireTableChanged()
        {
            fireTableChanged(new TableModelEvent(this));
        }

        public void fireTableChanged(TableModelEvent pEvent)
        {
            for (int i = 0; i < iTableModelListeners.size(); i++)
            {
                ((TableModelListener) iTableModelListeners.get(i))
                        .tableChanged(pEvent);
            }
        }

    }

    private class D2StashFilter implements ActionListener, DocumentListener
    {
        public void actionPerformed(ActionEvent pEvent)
        {
	    updateGUI(pEvent);

	    // apply filter
	    // TODO this name is misleading. should use a better method name.
            itemListChanged();
        }

	/**
	 * some GUI radio/checkbox grouping logic
	 */
	private void updateGUI(ActionEvent pEvent)
	{
	    /*
	     * socketed filter
	     */
	    // deselect "All" if one of the number sock is selected.
	    if (iCatSockAll.isSelected()) {
		if (iCatSock1.isSelected() || iCatSock2.isSelected() ||
		    iCatSock3.isSelected() || iCatSock4.isSelected() ||
		    iCatSock5.isSelected() || iCatSock6.isSelected()) {
		    iCatSockAll.setSelected(false);
		}
	    }
	    // deselect all number sock, when user select "All"
	    if (pEvent.getSource().equals(iCatSockAll)) {
		if(! iCatSockAll.isSelected()){
		    iCatSockAll.setSelected(true);
		    iCatSock1.setSelected(false);
		    iCatSock2.setSelected(false);
		    iCatSock3.setSelected(false);
		    iCatSock4.setSelected(false);
		    iCatSock5.setSelected(false);
		    iCatSock6.setSelected(false);
		}
	    }

	    /*
	     * quality filter (first row in GUI)
	     */
	    // deselect "All" when one of the quality is selected.
	    if (iQualAll.isSelected()) {
		if (iQualNorm.isSelected() ||
		    iQualExce.isSelected() ||
		    iQualEli.isSelected() ||
		    iQualOther.isSelected()) {
		    iQualAll.setSelected(false);
		}
	    }
	    // deselect all qualities when "All" is selected.
	    if (pEvent.getSource().equals(iQualAll)) {
		if (! iQualAll.isSelected()){
		    iQualAll.setSelected(true);
		    iQualNorm.setSelected(false);
		    iQualExce.setSelected(false);
		    iQualEli.setSelected(false);
		    iQualOther.setSelected(false);
		}
	    }
	    // select "All" when none of other quality is selected.
	    if (! (iQualNorm.isSelected() ||
		   iQualExce.isSelected() ||
		   iQualEli.isSelected() ||
		   iQualOther.isSelected())) {
		iQualAll.setSelected(true);
	    }

	    /*
	     * item types filter.
	     *
	     * when a item type filter is selected, highlight that filter's
	     * label and show the sub filter.
	     *
	     * edit: highlight part was removed. it doesn't add functionality
	     * and red color only brings anxious.
	     */
	    iArmorFilter.setVisible(iCatArmor.isSelected());
	    // iCatArmor.setForeground(iCatArmor.isSelected() ? Color.red : Color.black);
	    iWeaponFilter.setVisible(iCatWeapons.isSelected());
	    iSocketFilter.setVisible(iCatSocket.isSelected());
	    iCharmFilter.setVisible(iCatCharm.isSelected());
	    iMiscFilter.setVisible(iCatMisc.isSelected());
	    iSockFilter.setVisible(iTypeSocketed.isSelected());
	}

        public void insertUpdate(DocumentEvent e)
        {
            // activate filters
            itemListChanged();
        }

        public void removeUpdate(DocumentEvent e)
        {
            // activate filters
            itemListChanged();
        }

        public void changedUpdate(DocumentEvent e)
        {
            // activate filters
            itemListChanged();
        }

    }

    public String getFileName()
    {
        return iFileName;
    }

    public boolean isModified()
    {
        return iStash.isModified();
    }

    public D2ItemList getItemLists()
    {
        return iStash;
    }

    public void closeView()
    {
        disconnect(null);

        iFileManager.removeFromOpenWindows(this);
    }

    public void resetCharacter(D2Character pChar)
    {
        throw new RuntimeException("Internal error: wrong calling");
    }

    public void resetStash(D2Stash pStash)
    {
    	iStash.listenItemListEvents();
        iStash = pStash;
        itemListChanged();
    }

    public D2ItemList getStash(){
    	return iStash;
    }

    public String getStashName(){
    	return iStashName;
    }


}

// Local Variables:
// c-basic-offset: 4
// End:
