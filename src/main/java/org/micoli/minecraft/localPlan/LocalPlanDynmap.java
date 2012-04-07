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
	protected static final String DEF_INFOWINDOW = "<div class=\"infowindow\"><span style=\"font-size:120%;\">%regionname%</span><br /> Owner <span style=\"font-weight:bold;\">%playerowners%</span><br />Flags<br /><span style=\"font-weight:bold;\">%flags%</span></div>";

	/** The local plan. */
	LocalPlan localPlan;

	/** The infowindow. */
	String infowindow;

	private String lastWorldId;
	private Parcel lastParcel;

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

		String v = "<div class=\"regioninfo\">--" + this.infowindow + "</div>";
		v = v.replace("%regionname%", m.getLabel());
		v = v.replace("%price%", String.format("%01.2f", this.lastParcel.getPrice()));
		v = v.replace("%status%", this.lastParcel.getStatus().toString());

		StringBuilder str = new StringBuilder();
		Set<String> output = region.getOwners().getPlayers();
		for (Iterator<String> it = output.iterator(); it.hasNext();) {
			String name = it.next();
			str.append(name+"&nbsp;<img src='tiles/faces/16x16/" + name + ".png' />");
			if (it.hasNext()) {
				str.append(", ");
			}
		}
		
		v = v.replace("%playerowners%", str);
		// v = v.replace("%playerowners%",
		// region.getOwners().toPlayersString());
		v = v.replace("%groupowners%", region.getOwners().toGroupsString());
		v = v.replace("%playermembers%", region.getMembers().toPlayersString());
		v = v.replace("%groupmembers%", region.getMembers().toGroupsString());
		if (region.getParent() != null)
			v = v.replace("%parent%", region.getParent().getId());
		else
			v = v.replace("%parent%", "");
		v = v.replace("%priority%", String.valueOf(region.getPriority()));
		Map<Flag<?>, Object> map = region.getFlags();
		String flgs = "";
		for (Flag<?> f : map.keySet()) {
			flgs += f.getName() + ": " + map.get(f).toString() + "<br/>";
		}
		v = v.replace("%flags%", flgs);
		return v;
	}

	@Override
	protected void addStyle(String resid, String worldid, AreaMarker m, ProtectedRegion region) {
		this.lastWorldId = worldid;
		this.lastParcel = localPlan.getParcel(this.lastWorldId, region.getId());
		
		super.addStyle(resid, worldid, m, region);
		
		m.setLineStyle(1, 0.5, 128);
		m.setFillStyle(0.5, 128);
	}

}