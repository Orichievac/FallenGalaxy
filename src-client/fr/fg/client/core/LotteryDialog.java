/*
Copyright 2011 Jeremie Gottero
 
This file is part of Fallen Galaxy.
 
Fallen Galaxy is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
 
Fallen Galaxy is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.
 
You should have received a copy of the GNU Affero General Public License
along with Fallen Galaxy. If not, see <http://www.gnu.org/licenses/>.
*/
 
package fr.fg.client.core;
 
import java.util.HashMap;
 
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
 
import fr.fg.client.ajax.Action;
import fr.fg.client.ajax.ActionCallbackAdapter;
import fr.fg.client.animation.ToolTipTimeUpdater;
import fr.fg.client.data.AnswerData;
import fr.fg.client.data.LotteryInfoData;
import fr.fg.client.i18n.Formatter;
import fr.fg.client.i18n.StaticMessages;
import fr.fg.client.openjwt.OpenJWT;
import fr.fg.client.openjwt.animation.TimerManager;
import fr.fg.client.openjwt.core.Config;
import fr.fg.client.openjwt.ui.JSButton;
import fr.fg.client.openjwt.ui.JSComboBox;
import fr.fg.client.openjwt.ui.JSDialog;
import fr.fg.client.openjwt.ui.JSLabel;
import fr.fg.client.openjwt.ui.JSOptionPane;
import fr.fg.client.openjwt.ui.JSRowLayout;
 

public class LotteryDialog extends JSDialog implements ClickListener {
        // ------------------------------------------------------- CONSTANTES -- //
       
        public final static String SIGNS = "ΦΔΣΨ";
       
        private final static int[][] TICKETS = {
                // [cout crédit, gain]
        	{1000, 2}, 
        	{10000000, 4}, 
        	{1000, 10}, 
        	{10000000, 15},
        };
       
        // -------------------------------------------------------- ATTRIBUTS -- //
       
        private int idFleet;
       
        private JSButton buyBt;
       
        private JSButton[] numbersBt;
       
        private JSComboBox ticketComboBox;
       
        private ToolTipTimeUpdater timeUpdater;
       
        private JSLabel boughtTicketLabel;
       
        private Action currentAction;
       
        private JSLabel numberLabel;
       
        private boolean ticketBought;
       
        // ---------------------------------------------------- CONSTRUCTEURS -- //
       
        public LotteryDialog(int idFleet, LotteryInfoData infoData) {
                super(StaticMessages.INSTANCE.lottery(), true, true, true);
               
                this.idFleet = idFleet;
                this.ticketBought = infoData.hasBoughtTicket();
               
                String id = ToolTipTimeUpdater.generateId();
               
                JSLabel nextDrawLabel = new JSLabel("Prochain tirage dans <span id=\"" + id + "\">" +
                        Formatter.formatTime(infoData.getDrawRemainingTime(), true) + "</span>");
                nextDrawLabel.setPixelWidth(300);
                nextDrawLabel.setAlignment(JSLabel.ALIGN_CENTER);
               
                JSLabel chancesLabel = new JSLabel("Une chance sur 4 de gagner !");
                chancesLabel.setPixelWidth(300);
                chancesLabel.setAlignment(JSLabel.ALIGN_CENTER);
               
                timeUpdater = new ToolTipTimeUpdater(nextDrawLabel.getElement(), id,
                                infoData.getDrawRemainingTime(), true);
                TimerManager.register(timeUpdater);
               
                HTMLPanel descriptionPanel = new HTMLPanel(
                        "Achetez un ticket pour tenter de gagner des jours de Premium ou un pourcentage d'XP de votre Niveau !<br/>" +
                        "Un ticket plus cher augmente vos gains potentiels, " +
                        "mais vos chances de gagner restent les mêmes. " +
                        "Un ticket max. par tirage.");
                OpenJWT.setElementFloat(descriptionPanel.getElement(), "left");
                descriptionPanel.setWidth("340px");
                descriptionPanel.getElement().setAttribute("unselectable", "on");
                            
                numberLabel = new JSLabel("Choisissez votre numéro favori :");
                numberLabel.setPixelWidth(260);
                numberLabel.setAlignment(JSLabel.ALIGN_CENTER);
               
                numbersBt = new JSButton[4];
               
                for (int i = 0; i < numbersBt.length; i++) {
                        numbersBt[i] = new JSButton(String.valueOf(SIGNS.charAt(i)));
                        numbersBt[i].addStyleName("lotteryBall");
                        numbersBt[i].setPixelWidth(30);
                        numbersBt[i].addClickListener(this);
                }
               
                ticketComboBox = new JSComboBox();
                for (int i = 0; i < TICKETS.length; i++) {
                	if (i < 2) {
                        ticketComboBox.addItem("<div><div style=\"float: right;\">Gagnez " +
                                TICKETS[i][1] + " jours de Premium !&nbsp;</div>Ticket à " +
                                Formatter.formatNumber(TICKETS[i][0], true) +
                                " " + Utilities.getCreditsImage() + "</div>");
} else {
                        ticketComboBox.addItem("<div><div style=\"float: right;\">Gagnez " +
                                TICKETS[i][1] + "% d'XP !&nbsp;</div>Ticket à " +
                                Formatter.formatNumber(TICKETS[i][0], true) +
                                " " + Utilities.getCreditsImage() + "</div>");
}
                }
                ticketComboBox.setPixelWidth(260);
               
                buyBt = new JSButton("Acheter");
                buyBt.setPixelWidth(100);
                buyBt.addClickListener(this);
               
                boughtTicketLabel = new JSLabel();
                boughtTicketLabel.setPixelWidth(360);
                boughtTicketLabel.setVisible(false);
                boughtTicketLabel.setAlignment(JSLabel.ALIGN_CENTER);
               
                JSRowLayout layout = new JSRowLayout();
                layout.addRowSeparator(15);
                layout.addComponent(JSRowLayout.createHorizontalSeparator(80));
                layout.addComponent(nextDrawLabel);
                layout.addRow();
                layout.addComponent(JSRowLayout.createHorizontalSeparator(80));
                layout.addComponent(chancesLabel);
                layout.addRowSeparator(30);
                layout.addComponent(descriptionPanel);
                layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
                layout.addRowSeparator(20);
                layout.addComponent(numberLabel);
                layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
                layout.addRow();
                layout.addComponent(JSRowLayout.createHorizontalSeparator(18));
                for (int i = 0; i < numbersBt.length; i++) {
                        layout.addComponent(numbersBt[i]);
                }
                layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
                layout.addRowSeparator(20);
                layout.addComponent(ticketComboBox);
                layout.addComponent(buyBt);
                layout.addComponent(boughtTicketLabel);
                layout.setRowAlignment(JSRowLayout.ALIGN_CENTER);
                layout.addRowSeparator(20);
               
                layout.getElement().getStyle().setProperty("background",
                                "url('" + Config.getMediaUrl() +
                                "images/misc/lottery.png') 0 0 no-repeat");
               
                setComponent(layout);
                centerOnScreen();
                setDefaultCloseOperation(DESTROY_ON_CLOSE);
               
                updateState(infoData);
        }
       
        // --------------------------------------------------------- METHODES -- //
       
        @Override
        public void setVisible(boolean visible) {
                super.setVisible(visible);
               
                if (!visible) {
                        if (timeUpdater != null) {
                                TimerManager.unregister(timeUpdater);
                                timeUpdater.destroy();
                                timeUpdater = null;
                        }
                       
                        ticketComboBox = null;
                        boughtTicketLabel = null;
                        currentAction = null;
                        buyBt = null;
                }
        }
       
      
        public void onClick(Widget sender) {
                if (sender == buyBt) {
                        if (currentAction != null && currentAction.isPending())
                                return;
                       
                        int number = -1;
                        for (int i = 0; i < numbersBt.length; i++) {
                                if (numbersBt[i].getStyleName().contains("lotteryBall-selected")) {
                                        number = i;
                                        break;
                                }
                        }
                       
                        if (number == -1) {
                                JSOptionPane.showMessageDialog("Choisissez un symbole",
                                        "Erreur", JSOptionPane.OK_OPTION, JSOptionPane.WARNING_MESSAGE, null);
                                return;
                        }
                       
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("fleet", String.valueOf(idFleet));
                        params.put("level", String.valueOf(ticketComboBox.getSelectedIndex()));
                        params.put("number", String.valueOf(number));
                       
                        currentAction = new Action("lottery/buyticket", params, new ActionCallbackAdapter() {
                                @Override
                                public void onSuccess(AnswerData data) {
                                        updateState(data.getLotteryInfoData());
                                }
                        });
                } else {
                        if (!ticketBought) {
                                for (int i = 0; i < numbersBt.length; i++) {
                                        if (sender == numbersBt[i]) {
                                                for (int j = 0; j < numbersBt.length; j++) {
                                                        if (i == j)
                                                                numbersBt[j].addStyleName("lotteryBall-selected");
                                                        else
                                                                numbersBt[j].removeStyleName("lotteryBall-selected");
                                                }
                                                break;
                                        }
                                }
                        }
                }
        }
       
        // ------------------------------------------------- METHODES PRIVEES -- //
       
        private void updateState(LotteryInfoData infoData) {
                this.ticketBought = infoData.hasBoughtTicket();
               
                if (infoData.hasBoughtTicket()) {
                        for (int i = 0; i < numbersBt.length; i++) {
                                if (infoData.getBoughtTicketNumber() == i)
                                        numbersBt[i].addStyleName("lotteryBall-selected");
                                else
                                        numbersBt[i].removeStyleName("lotteryBall-selected");
                        }
                       
                        numberLabel.setText("Votre symbole favori est le :");
                        if (infoData.getBoughtTicketLevel() < 2){ // 1er cas
                        boughtTicketLabel.setText("Vous avez acheté un ticket. " +
                                TICKETS[infoData.getBoughtTicketLevel()][1] +
                                " jours de Premium à gagner !");
                        }
                        else { // 2ème cas
                        boughtTicketLabel.setText("Vous avez acheté un ticket. " +
                                TICKETS[infoData.getBoughtTicketLevel()][1] +
                                " % d'XP à gagner !");
                        }                
                        
                        boughtTicketLabel.setVisible(true);
                        ticketComboBox.setVisible(false);
                        buyBt.setVisible(false);
                } else {
                        numberLabel.setText("Choisissez votre symbole favori :");
                        boughtTicketLabel.setVisible(false);
                        ticketComboBox.setVisible(true);
                        buyBt.setVisible(true);
                }
        }
}