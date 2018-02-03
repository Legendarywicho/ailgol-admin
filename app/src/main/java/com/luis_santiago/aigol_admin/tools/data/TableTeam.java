package com.luis_santiago.aigol_admin.tools.data;
import java.util.*;

import static com.luis_santiago.aigol_admin.R.id.team;

/**
 * Created by legendarywicho on 9/6/17.
 */

public class TableTeam {
    private String id;
    private String position;
    private String logo;
    private String name;
    private String mp;
    private String gf;
    private String ga;
    private String gd;
    private String pts;

    public TableTeam(String id, String position, String logo, String name, String mp, String gf, String ga, String gd, String pts) {
        this.id = id;
        this.position = position;
        this.logo = logo;
        this.name = name;
        this.mp = mp;
        this.gf = gf;
        this.ga = ga;
        this.gd = gd;
        this.pts = pts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMp() {
        return mp;
    }

    public void setMp(String mp) {
        this.mp = mp;
    }

    public String getGf() {
        return gf;
    }

    public void setGf(String gf) {
        this.gf = gf;
    }

    public String getGa() {
        return ga;
    }

    public void setGa(String ga) {
        this.ga = ga;
    }

    public String getGd() {
        return gd;
    }

    public void setGd(String gd) {
        this.gd = gd;
    }

    public String getPts() {
        return pts;
    }

    public void setPts(String pts) {
        this.pts = pts;
    }

    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("goal_afer", ga);
        result.put("goal_difference", gd);
        result.put("goal_for", gf);
        result.put("matcher_played", mp);
        result.put("name", name);
        result.put("points", pts);
        result.put("position", position);
        result.put("team_logo", logo);
        return  result;
    }
}
