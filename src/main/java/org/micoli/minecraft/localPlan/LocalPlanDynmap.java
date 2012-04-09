package org.micoli.minecraft.localPlan;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.dynmap.markers.AreaMarker;
import org.dynmap.worldguard.DynmapWorldGuardPlugin;
import org.micoli.minecraft.localPlan.entities.Parcel;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * The Class LocalPlanDynmap.
 */
public class LocalPlanDynmap extends DynmapWorldGuardPlugin {

	/** The Constant DEF_INFOWINDOW. */
	protected static final String DEF_INFOWINDOW = "<div class=\"infowindow\"><span style=\"font-size:120%;\"><u><b>%regionname%</b></u></span><br /><span style=\"font-size:120%;\">%status% (%price%)<br /> Owner<br /><span style=\"font-weight:bold;\">%playerowners%</span><br /> Members<br /><span style=\"font-weight:bold;\">%playermembers%</span><br /><span style=\"font-weight:bold;\">%flags%</span></div>";

	/** The local plan. */
	private LocalPlan localPlan;

	/** The infowindow. */
	private String infowindow;

	private Parcel lastParcel;
	private boolean withFlags;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dynmap.worldguard.DynmapWorldGuardPlugin#onEnable()
	 */
	@Override
	public void onEnable() {
		super.onEnable();
		FileConfiguration cfg = getConfig();
		infowindow = cfg.getString("infowindow", DEF_INFOWINDOW);
		localPlan = (LocalPlan) getServer().getPluginManager().getPlugin("LocalPlan");
		withFlags = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dynmap.worldguard.DynmapWorldGuardPlugin#formatInfoWindow(com.sk89q
	 * .worldguard.protection.regions.ProtectedRegion,
	 * org.dynmap.markers.AreaMarker)
	 */
	@Override
	public String formatInfoWindow(ProtectedRegion region, AreaMarker m) {

		String v = "<div class=\"regioninfo\">" + this.infowindow + "</div>";
		v = v.replace("%regionname%", m.getLabel());
		v = v.replace("%price%", String.format("%01.2f", this.lastParcel.getPrice()));
		v = v.replace("%status%", this.lastParcel.getBuyStatus().toString());

		StringBuilder strPlayers = new StringBuilder();

		Set<String> output = region.getOwners().getPlayers();
		for (Iterator<String> it = output.iterator(); it.hasNext();) {
			String name = it.next();
			strPlayers.append(name+"&nbsp;<img src='tiles/faces/16x16/" + name + ".png' />");
			if (it.hasNext()) {
				strPlayers.append(", ");
			}
		}
		
		v = v.replace("%playerowners%", strPlayers);
		// v = v.replace("%playerowners%",
		// region.getOwners().toPlayersString());
		v = v.replace("%groupowners%", region.getOwners().toGroupsString());
		v = v.replace("%playermembers%", region.getMembers().toPlayersString());
		v = v.replace("%groupmembers%", region.getMembers().toGroupsString());
		v = v.replace("%parent%", "");
		v = v.replace("%priority%", "");

		String flgs = "";
		if(withFlags){
			Map<Flag<?>, Object> map = region.getFlags();
			for (Flag<?> f : map.keySet()) {
				flgs += f.getName() + ": " + map.get(f).toString() + "<br/>";
			}
		}
		v = v.replace("%flags%", flgs);
		return v;
	}

	@Override
	protected void addStyle(String resid, String worldid, AreaMarker m, ProtectedRegion region) {
		this.lastParcel = localPlan.getParcel(worldid, region.getId());
		
		super.addStyle(resid, worldid, m, region);
		
		int fillColor = 0;
		int lineColor = 0;
		switch (this.lastParcel.getBuyStatus()){
			case BUYABLE:
				fillColor = Integer.parseInt("#008000".substring(1), 16);
			break;	
			case UNBUYABLE:
				fillColor = Integer.parseInt("#AA0000".substring(1), 16);
			break;	
		}
		switch(this.lastParcel.getOwnerType()){
			case PLAYER :
				lineColor = Integer.parseInt("#008000".substring(1), 16);
			break;
			case FACTION:
				lineColor = Integer.parseInt("#008000".substring(1), 16);
			break;
			case STATE:
				lineColor = Integer.parseInt("#008000".substring(1), 16);
			break;
		}
		m.setLineStyle(1, 0.35, lineColor);
		m.setFillStyle(0.35, fillColor);
	}

}