package com.transfer.process;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

import static java.util.Objects.*;

public class ClubLeague {
    private Connection conn;
    public ClubLeague() {
        setDBConnection();
    }
    private void setDBConnection(){
        Properties connConfig = new Properties();
        connConfig.setProperty("user", "");
        connConfig.setProperty("password", "");

        try {
            conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/soccer_transfers?useSSL=false", connConfig);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void performInsertion(){
        Path path = Paths.get("club_league.csv");

        try {
            PreparedStatement club_stmnt = conn.prepareStatement(
                    "INSERT INTO club (club_name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            PreparedStatement league_stmt = conn.prepareStatement(
                    "INSERT INTO league (league_name, code_name, country) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            PreparedStatement club_league_stmt = conn.prepareStatement(
                    "INSERT IGNORE INTO club_league (idclub, idleague, season) VALUES (?, ?, ?)");

            Map<String, Integer>clubs = new HashMap();
            Map<String, Integer>leagues = new HashMap();

            BufferedReader reader = Files.newBufferedReader(path);

            reader.lines().forEach(l -> {
                if(l.contains("\"")){
                    l = l.replaceAll("\"([^,]+)(,)(.+?)\"","$1$3");
                }
                l = StringUtils.stripAccents(l);

                String values[] = l.split(",");
                Integer clubId = clubs.get(values[0]);
                Integer leagueId = leagues.get(values[2]);

                try {
                    if(isNull(clubId)){
                        club_stmnt.setString(1,values[0]);

                        club_stmnt.executeUpdate();
                        ResultSet rs_club = club_stmnt.getGeneratedKeys();
                        rs_club.next();
                        clubId = rs_club.getInt(1);
                        clubs.put(values[0], clubId);
                    }
                    if(isNull(leagueId)){
                        league_stmt.setString(1,values[1]);
                        league_stmt.setString(2,values[2]);
                        league_stmt.setString(3,values[3]);

                        league_stmt.executeUpdate();
                        ResultSet rs_league = league_stmt.getGeneratedKeys();
                        rs_league.next();
                        leagueId = rs_league.getInt(1);

                        leagues.put(values[2], leagueId);
                    }

                    club_league_stmt.setInt(1, clubId);
                    club_league_stmt.setInt(2, leagueId);
                    club_league_stmt.setString(3, values[4].replace("/", ""));
                    club_league_stmt.executeUpdate();
                } catch (SQLException e) {
                    System.out.println(l);
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException|SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
