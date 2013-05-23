/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc, Thierry Chevalier

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

package fr.fg.server.action.login;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.mail.MessagingException;

import org.apache.commons.lang.RandomStringUtils;

import fr.fg.server.data.AccountAction;
import fr.fg.server.data.Connection;
import fr.fg.server.data.DataAccess;
import fr.fg.server.data.IllegalOperationException;
import fr.fg.server.data.Player;
import fr.fg.server.i18n.Badwords;
import fr.fg.server.i18n.Messages;
import fr.fg.server.servlet.Action;
import fr.fg.server.servlet.Session;
import fr.fg.server.util.Config;
import fr.fg.server.util.LoggingSystem;
import fr.fg.server.util.Mailer;
import fr.fg.server.util.Utilities;

public class Register extends Action {
	// ------------------------------------------------------- CONSTANTES -- //
	
	private final static String[] KEYWORDS = {
		"administrator", "true", "false", "guest", "moderator", "test", "null", "admin","anonymous",
		"moderateur", "administrateur"
	};
	
	// -------------------------------------------------------- ATTRIBUTS -- //
	private final int PREMIUM_TIME = 604800; //On met 7 jours de Premium à chaque nouveau joueur et au sponsor (s'il y en a un)
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@Override
	protected String execute(Player player, Map<String, Object> params,
			Session session) throws Exception {
		// Inscription d'un nouveau joueur
		String login = (String) params.get("login");
		String password = (String) params.get("password");
		String email = (String) params.get("email");
		String birthday = (String) params.get("birthday");
		String sponsor = (String) params.get("sponsor");
		int idSponsor = 0;
		Player sponsorPlayer;
		// Vérifie que le login est autorisé
		for (String keyword : KEYWORDS) {
			if (login.equals(keyword))
				throw new IllegalOperationException(
						Messages.getString("common.illegalLogin"));
		}
		
		if (Badwords.containsBadwords(login))
			throw new IllegalOperationException(
					Messages.getString("common.illegalLogin"));
		
		Player existingPlayer = DataAccess.getPlayerByLogin(login);
		
		// Vérifie que le login n'est pas déjà utilisé
		if (existingPlayer != null)
			throw new IllegalOperationException(
					Messages.getString("common.loginInUse"));
		
		// Vérifie que la date de naissance est correcte
		String dateFormat = Messages.getString("common.dateFormat");
		int birthdayDate = 0;
		
		if (birthday.length() > 0) {
			SimpleDateFormat format = new SimpleDateFormat(dateFormat);
			
			try {
				Date date = format.parse(birthday);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				birthdayDate =
					calendar.get(Calendar.YEAR)* 10000 +
					(1 + calendar.get(Calendar.MONTH)) * 100 +
					calendar.get(Calendar.DAY_OF_MONTH);
			} catch (Exception e) {
				throw new IllegalOperationException(
						Messages.getString("common.invalidBirthday"));
			}
		}
		
		// Vérifie que l'email n'est pas déjà utilisé
		existingPlayer = DataAccess.getPlayerByEmail(email);
		
		if (existingPlayer != null)
			throw new IllegalOperationException(
					Messages.getString("common.emailInUse"));
		
		if(sponsor.length()>0)
		{
			Integer integerIp = (Integer) session.getAttribute("ip");
			sponsorPlayer = DataAccess.getPlayerByLogin(sponsor);
			if(sponsorPlayer!=null && !sponsorPlayer.isAi()) { //Le parrain existe
				idSponsor = sponsorPlayer.getId();
				if(Connection.compare(integerIp, sponsorPlayer.getId(),Utilities.now()+(45* 24*3600) )) {
					//L'ip du filleul est egale à une ip du parrain 
					try {
						Mailer.sendMail("admin@fallengalaxy.eu",
							"Tentative d'abus de parrainage par ip",
							"Le nouveau joueur "+login+" a une IP identique à son parrain "+sponsorPlayer.getLogin()+ "("+long2ip(integerIp)+")");
					} catch (MessagingException e) {
						LoggingSystem.getServerLogger().error("Erreur lors de l'envoi d'un mail de notification à l'administrateur:\n" +
								"Sujet: Tentative d'abus de parrainage par ip\n"+
								"Corps: Le nouveau joueur "+login+" a une IP identique à son parrain "+sponsorPlayer.getLogin()+ "("+long2ip(integerIp)+")",e);
					}
				}
	
				synchronized (sponsorPlayer.getLock())
				{
					sponsorPlayer = DataAccess.getEditable(sponsorPlayer);
					long time = sponsorPlayer.getEndPremium()>Utilities.now()? sponsorPlayer.getEndPremium() : Utilities.now();
					sponsorPlayer.setEndPremium(time+PREMIUM_TIME);
					if(sponsorPlayer.getRights()<1) {
						sponsorPlayer.setRights(1);
					}
					sponsorPlayer.save();
				}
				
				LoggingSystem.getServerLogger().info(sponsorPlayer.getLogin()+ " est le parrain de "+login+"("+long2ip(integerIp)+")");
			}
			else
			{
				throw new IllegalOperationException("Le parrain n'existe pas!");
			}
		}
		
		// Crée le joueur
		String hash;
		if (Config.isDebug()) {
			hash = "";
		} else {
			do {
				hash = RandomStringUtils.randomAlphanumeric(32);
			} while (DataAccess.getPlayerByRegistrationHash(hash) != null);
		}
		
		String ekey;
		do {
			ekey = RandomStringUtils.randomAlphanumeric(32);
		} while (DataAccess.getPlayerByEkey(ekey) != null);
		
		// Envoie un email de confirmation
		if (!Config.isDebug()) {
			try {
				String content = Config.getOpeningDate()< Utilities.now()? // La date d'ouverture est-elle inferieur à maintenant ?
						Messages.getString("common.emailContent",
								login, Config.getServerURL() + "confirm/" +
								hash, login, password, Config.getServerName())
								:
						Messages.getString("common.emailContentNotOpened",
								login, Config.getServerURL() + "confirm/" +
								hash, login, password, Config.getServerName());
						Mailer.sendMail(email,Messages.getString("common.emailSubject",login),content);
			} catch (MessagingException e) {
				LoggingSystem.getServerLogger().error("Could not send registration email.", e);
				throw new IllegalOperationException(
						Messages.getString("common.emailSentFailed"));
			}
		}
		
		Player newPlayer = new Player(login,
				Utilities.encryptPassword(password), email, ekey, hash);
		if (Config.isDebug())
			newPlayer.setRights(Player.PREMIUM | Player.MODERATOR |
				Player.ADMINISTRATOR | Player.SUPER_ADMINISTRATOR);
		else
			newPlayer.setRights(1);
		newPlayer.setBirthday(birthdayDate);
		newPlayer.setEndPremium(Utilities.now()+PREMIUM_TIME);
		newPlayer.setIdSponsor(idSponsor);
		newPlayer.save();
		
		// Log l'adresse IP pour l'enregistrement du compte
		int ip = (Integer) session.getAttribute("ip");
		Connection connection = new Connection(ip, newPlayer.getId());
		connection.setEnd(connection.getStart());
		connection.save();
		
		AccountAction accountAction = new AccountAction(login,
			AccountAction.ACTION_REGISTERED, email, birthdayDate, ip, 0, "");
		accountAction.save();
		
		return FORWARD_SUCCESS;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	private String long2ip(long ip) throws UnknownHostException
	   {
	      byte[] bytes=new byte[4];
	      
	      bytes[0]=(byte) ((ip & 0xff000000)>>24);
	      bytes[1]=(byte) ((ip & 0x00ff0000)>>16);
	      bytes[2]=(byte) ((ip & 0x0000ff00)>>8);
	      bytes[3]=(byte) (ip & 0x000000ff);
	      
	      return InetAddress.getByAddress(bytes).getHostAddress();
		
	}
}
