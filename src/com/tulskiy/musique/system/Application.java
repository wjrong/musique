/*
 * Copyright (c) 2008, 2009, 2010 Denis Tulskiy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @Author: Denis Tulskiy
 * @Date: Oct 30, 2009
 */
package com.tulskiy.musique.system;

import com.tulskiy.musique.audio.player.Player;
import com.tulskiy.musique.gui.MainWindow;
import com.tulskiy.musique.playlist.PlaylistManager;
import com.tulskiy.musique.playlist.Track;
import org.jaudiotagger.tag.id3.valuepair.TextEncoding;

import javax.swing.*;

public class Application {
    private static Application ourInstance = new Application();

    private Player player;
    private Configuration configuration;
    private PlaylistManager playlistManager;
    private MainWindow mainWindow;

    public static Application getInstance() {
        return ourInstance;
    }

    private Application() {

    }

    public void load() {
        configuration = new Configuration();
        configuration.load();

        playlistManager = new PlaylistManager();
        playlistManager.loadPlaylists();

        player = new Player();

        loadSettings();
    }

    private void loadSettings() {
        player.setVolume(configuration.getDouble("player.volume", 1));
        UIManager.put("Slider.paintValue", Boolean.FALSE);
        TextEncoding.getInstanceOf().setDefaultNonUnicode(
                configuration.getString("tag.defaultEncoding", "windows-1251"));

        try {
            String laf = configuration.getString("gui.LAF", "");
            if (laf.isEmpty()) {
                laf = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
            }
            UIManager.setLookAndFeel(laf);
        } catch (Exception e) {
            System.err.println("Could not load LaF: " + e.getCause());
        }
    }

    private void saveSettings() {
        configuration.setDouble("player.volume", player.getVolume());
//        Track lastPlayed = player.getSong();
//        if (lastPlayed != null) {
        //todo fix me here
//            configuration.setInt("player.lastPlayed", lastPlayed.getSongID());
//        }
        configuration.setString("gui.LAF", UIManager.getLookAndFeel().getClass().getCanonicalName());
    }

    public void start() {
        try {
            if (mainWindow != null) {
                mainWindow.shutdown();
                mainWindow = null;
            }

            mainWindow = new MainWindow();
            mainWindow.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        player.stop();
        if (mainWindow != null)
            mainWindow.shutdown();
        playlistManager.savePlaylists();
        saveSettings();
        configuration.save();
        System.exit(0);
    }

    public Player getPlayer() {
        return player;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public PlaylistManager getPlaylistManager() {
        return playlistManager;
    }
}
