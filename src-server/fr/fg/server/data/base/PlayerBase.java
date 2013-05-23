/*
Copyright 2010 Jeremie Gottero

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

package fr.fg.server.data.base;

import fr.fg.server.dao.PersistentData;

public class PlayerBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private int id;
	private String login;
	private String password;
	private String ekey;
	private long banChat;
	private long banGame;
	private String avatar;
	private boolean ai;
	private boolean sex;
	private int rights;
	private int premiumHours;
	private String email;
	private String registration;
	private String recoverEmail;
	private String recoverPassword;
	private String closeAccountHash;
	private String closeAccountReason;
	private long birthday;
	private int allyRank;
	private long points;
	private long xp;
	private long credits;
	private long eventsReadDate;
	private long tutorial;
	private boolean diplomacyActivated;
	private long switchDiplomacyDate;
	private boolean settingsGridVisible;
	private int settingsBrightness;
	private int settingsFleetSkin;
	private int settingsMusic;
	private String settingsTheme;
	private int settingsChat;
	private boolean settingsCensorship;
	private int settingsGeneralVolume;
	private int settingsSoundVolume;
	private int settingsMusicVolume;
	private int settingsGraphics;
	private boolean settingsOptimizeConnection;
	private String settingsFleetNamePrefix;
	private int settingsFleetNameSuffix;
	private long lastConnection;
	private int idAlly;
	private long registrationDate;
	private long idContract;
	private long endPremium;
	private long lastAllyIn;
	private int idSponsor;
	private String description;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (id < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				id + "' (must be >= 0).");
		else
			this.id = id;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (login == null)
			throw new IllegalArgumentException("Invalid value: '" +
				login + "' (must not be null).");
		else
			this.login = login;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (password == null)
			throw new IllegalArgumentException("Invalid value: '" +
				password + "' (must not be null).");
		else
			this.password = password;
	}
	
	public String getEkey() {
		return ekey;
	}
	
	public void setEkey(String ekey) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (ekey == null)
			throw new IllegalArgumentException("Invalid value: '" +
				ekey + "' (must not be null).");
		else
			this.ekey = ekey;
	}
	
	public long getBanChat() {
		return banChat;
	}
	
	public void setBanChat(long banChat) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (banChat < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				banChat + "' (must be >= 0).");
		else
			this.banChat = banChat;
	}
	
	public long getBanGame() {
		return banGame;
	}
	
	public void setBanGame(long banGame) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (banGame < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				banGame + "' (must be >= 0).");
		else
			this.banGame = banGame;
	}
	
	public String getAvatar() {
		return avatar;
	}
	
	public void setAvatar(String avatar) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (avatar == null)
			throw new IllegalArgumentException("Invalid value: '" +
				avatar + "' (must not be null).");
		else
			this.avatar = avatar;
	}
	
	public boolean isAi() {
		return ai;
	}
	
	public void setAi(boolean ai) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.ai = ai;
	}
	
	public boolean isSex() {
		return sex;
	}
	
	public void setSex(boolean sex) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.sex = sex;
	}
	
	public int getRights() {
		return rights;
	}
	
	public void setRights(int rights) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (rights < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				rights + "' (must be >= 0).");
		else
			this.rights = rights;
	}

	public int getPremiumHours() {
		return premiumHours;
	}
	
	public void setPremiumHours(int premiumHours) {
		if (!isEditable())
			throwDataUneditableException();
		this.premiumHours = premiumHours;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (email == null)
			throw new IllegalArgumentException("Invalid value: '" +
				email + "' (must not be null).");
		else
			this.email = email;
	}
	
	public String getRegistration() {
		return registration;
	}
	
	public void setRegistration(String registration) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (registration == null)
			throw new IllegalArgumentException("Invalid value: '" +
				registration + "' (must not be null).");
		else
			this.registration = registration;
	}
	
	public String getRecoverEmail() {
		return recoverEmail;
	}
	
	public void setRecoverEmail(String recoverEmail) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (recoverEmail == null)
			throw new IllegalArgumentException("Invalid value: '" +
				recoverEmail + "' (must not be null).");
		else
			this.recoverEmail = recoverEmail;
	}
	
	public String getRecoverPassword() {
		return recoverPassword;
	}
	
	public void setRecoverPassword(String recoverPassword) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (recoverPassword == null)
			throw new IllegalArgumentException("Invalid value: '" +
				recoverPassword + "' (must not be null).");
		else
			this.recoverPassword = recoverPassword;
	}
	
	public String getCloseAccountHash() {
		return closeAccountHash;
	}
	
	public void setCloseAccountHash(String closeAccountHash) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (closeAccountHash == null)
			throw new IllegalArgumentException("Invalid value: '" +
				closeAccountHash + "' (must not be null).");
		else
			this.closeAccountHash = closeAccountHash;
	}
	
	public String getCloseAccountReason() {
		return closeAccountReason;
	}
	
	public void setCloseAccountReason(String closeAccountReason) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (closeAccountReason == null)
			throw new IllegalArgumentException("Invalid value: '" +
				closeAccountReason + "' (must not be null).");
		else
			this.closeAccountReason = closeAccountReason;
	}
	
	public long getBirthday() {
		return birthday;
	}
	
	public void setBirthday(long birthday) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (birthday < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				birthday + "' (must be >= 0).");
		else
			this.birthday = birthday;
	}
	
	public int getAllyRank() {
		return allyRank;
	}
	
	public void setAllyRank(int allyRank) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (allyRank < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				allyRank + "' (must be >= 0).");
		else
			this.allyRank = allyRank;
	}
	
	public long getPoints() {
		return points;
	}
	
	public void setPoints(long points) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (points < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				points + "' (must be >= 0).");
		else
			this.points = points;
	}
	
	public long getXp() {
		return xp;
	}
	
	public void setXp(long xp) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (xp < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				xp + "' (must be >= 0).");
		else
			this.xp = xp;
	}
	
	public long getCredits() {
		return credits;
	}
	
	public void setCredits(long credits) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (credits < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				credits + "' (must be >= 0).");
		else
			this.credits = credits;
	}
	
	public long getEventsReadDate() {
		return eventsReadDate;
	}
	
	public void setEventsReadDate(long eventsReadDate) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (eventsReadDate < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				eventsReadDate + "' (must be >= 0).");
		else
			this.eventsReadDate = eventsReadDate;
	}
	
	public long getTutorial() {
		return tutorial;
	}
	
	public void setTutorial(long tutorial) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (tutorial < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				tutorial + "' (must be >= 0).");
		else
			this.tutorial = tutorial;
	}
	
	public boolean isDiplomacyActivated() {
		return diplomacyActivated;
	}
	
	public void setDiplomacyActivated(boolean diplomacyActivated) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.diplomacyActivated = diplomacyActivated;
	}
	
	public long getSwitchDiplomacyDate() {
		return switchDiplomacyDate;
	}
	
	public void setSwitchDiplomacyDate(long switchDiplomacyDate) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (switchDiplomacyDate < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				switchDiplomacyDate + "' (must be >= 0).");
		else
			this.switchDiplomacyDate = switchDiplomacyDate;
	}
	
	public boolean isSettingsGridVisible() {
		return settingsGridVisible;
	}
	
	public void setSettingsGridVisible(boolean settingsGridVisible) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.settingsGridVisible = settingsGridVisible;
	}
	
	public int getSettingsBrightness() {
		return settingsBrightness;
	}
	
	public void setSettingsBrightness(int settingsBrightness) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (settingsBrightness < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				settingsBrightness + "' (must be >= 0).");
		else
			this.settingsBrightness = settingsBrightness;
	}
	
	public int getSettingsFleetSkin() {
		return settingsFleetSkin;
	}
	
	public void setSettingsFleetSkin(int settingsFleetSkin) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (settingsFleetSkin < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				settingsFleetSkin + "' (must be >= 0).");
		else
			this.settingsFleetSkin = settingsFleetSkin;
	}
	
	public int getSettingsMusic() {
		return settingsMusic;
	}
	
	public void setSettingsMusic(int settingsMusic) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (settingsMusic < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				settingsMusic + "' (must be >= 0).");
		else
			this.settingsMusic = settingsMusic;
	}
	
	public String getSettingsTheme() {
		return settingsTheme;
	}
	
	public void setSettingsTheme(String settingsTheme) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (settingsTheme == null)
			throw new IllegalArgumentException("Invalid value: '" +
				settingsTheme + "' (must not be null).");
		else
			this.settingsTheme = settingsTheme;
	}
	
	public int getSettingsChat() {
		return settingsChat;
	}
	
	public void setSettingsChat(int settingsChat) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (settingsChat < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				settingsChat + "' (must be >= 0).");
		else
			this.settingsChat = settingsChat;
	}
	
	public boolean isSettingsCensorship() {
		return settingsCensorship;
	}
	
	public void setSettingsCensorship(boolean settingsCensorship) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.settingsCensorship = settingsCensorship;
	}
	
	public int getSettingsGeneralVolume() {
		return settingsGeneralVolume;
	}
	
	public void setSettingsGeneralVolume(int settingsGeneralVolume) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (settingsGeneralVolume < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				settingsGeneralVolume + "' (must be >= 0).");
		else
			this.settingsGeneralVolume = settingsGeneralVolume;
	}
	
	public int getSettingsSoundVolume() {
		return settingsSoundVolume;
	}
	
	public void setSettingsSoundVolume(int settingsSoundVolume) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (settingsSoundVolume < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				settingsSoundVolume + "' (must be >= 0).");
		else
			this.settingsSoundVolume = settingsSoundVolume;
	}
	
	public int getSettingsMusicVolume() {
		return settingsMusicVolume;
	}
	
	public void setSettingsMusicVolume(int settingsMusicVolume) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (settingsMusicVolume < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				settingsMusicVolume + "' (must be >= 0).");
		else
			this.settingsMusicVolume = settingsMusicVolume;
	}
	
	public int getSettingsGraphics() {
		return settingsGraphics;
	}
	
	public void setSettingsGraphics(int settingsGraphics) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (settingsGraphics < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				settingsGraphics + "' (must be >= 0).");
		else
			this.settingsGraphics = settingsGraphics;
	}
	
	public boolean isSettingsOptimizeConnection() {
		return settingsOptimizeConnection;
	}
	
	public void setSettingsOptimizeConnection(boolean settingsOptimizeConnection) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.settingsOptimizeConnection = settingsOptimizeConnection;
	}
	
	public String getSettingsFleetNamePrefix() {
		return settingsFleetNamePrefix;
	}
	
	public void setSettingsFleetNamePrefix(String settingsFleetNamePrefix) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (settingsFleetNamePrefix == null)
			throw new IllegalArgumentException("Invalid value: '" +
				settingsFleetNamePrefix + "' (must not be null).");
		else
			this.settingsFleetNamePrefix = settingsFleetNamePrefix;
	}
	
	public int getSettingsFleetNameSuffix() {
		return settingsFleetNameSuffix;
	}
	
	public void setSettingsFleetNameSuffix(int settingsFleetNameSuffix) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (settingsFleetNameSuffix < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				settingsFleetNameSuffix + "' (must be >= 0).");
		else
			this.settingsFleetNameSuffix = settingsFleetNameSuffix;
	}
	
	public long getLastConnection() {
		return lastConnection;
	}
	
	public void setLastConnection(long lastConnection) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (lastConnection < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				lastConnection + "' (must be >= 0).");
		else
			this.lastConnection = lastConnection;
	}
	
	public int getIdAlly() {
		return idAlly;
	}
	
	public void setIdAlly(int idAlly) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idAlly < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idAlly + "' (must be >= 0).");
		else
			this.idAlly = idAlly;
	}
	
	public long getRegistrationDate() {
		return registrationDate;
	}
	
	public void setRegistrationDate(long registrationDate) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (registrationDate < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				registrationDate + "' (must be >= 0).");
		else
			this.registrationDate = registrationDate;
	}
	
	public long getIdContract() {
		return idContract;
	}
	
	public void setIdContract(long idContract) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (idContract < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				idContract + "' (must be >= 0).");
		else
			this.idContract = idContract;
	}
	
	public long getEndPremium() {
		return endPremium;
	}
	
	public void setEndPremium(long endPremium) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (endPremium < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				endPremium + "' (must be >= 0).");
		else
			this.endPremium = endPremium;
	}
	
	public long getLastAllyIn() {
		return lastAllyIn;
	}
	
	public void setLastAllyIn(long lastAllyIn) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (lastAllyIn < 0)
			throw new IllegalArgumentException("Invalid value: '" +
				lastAllyIn + "' (must be >= 0).");
		else
			this.lastAllyIn = lastAllyIn;
	}
	
	
	public int getIdSponsor() {
		return idSponsor;
	}
	
	public void setIdSponsor(int idSponsor) {
		if(!isEditable()) {
			throwDataUneditableException();
		}	
		
		if(idSponsor < 0)
			throw new IllegalArgumentException("Invalid value: '" +
					idSponsor + "' (must be >=0.");
		else {
			this.idSponsor = idSponsor;
		}
	}
	
	public void setDescription(String description) {
		if (!isEditable())
			throwDataUneditableException();
		
		if (description == null)
			throw new IllegalArgumentException("Invalid value: '" +
				description + "' (must not be null).");
		else
			this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	// ------------------------------------------------- METHODES PRIVEES -- //
}
