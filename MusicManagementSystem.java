package mysql;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.Instant;

public class MusicManagementSystem {

    private static Connection conn;

    public static void main(String[] args) {
        try {
            // Establish the database connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dhwani", "root", "Pallu@vaishu572");

            // Create frame
            JFrame frame = new JFrame("Music Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 500);
            frame.getContentPane().setBackground(Color.BLACK); // Set frame background to black
            frame.setLayout(new BorderLayout());

            // Create a header label for "Dhwani"
            JLabel headerLabel = new JLabel("Dhwani", SwingConstants.CENTER);
            headerLabel.setForeground(Color.GREEN); // Set text color to green
            headerLabel.setFont(new Font("Serif", Font.BOLD, 48)); // Set large bold font for header
            frame.add(headerLabel, BorderLayout.NORTH);

            // Create a panel with GridLayout for buttons
            JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10)); // Add vertical gap between buttons
            panel.setBackground(Color.BLACK); // Match panel background to frame

            // Create and customize action buttons
            JButton btnInsertUser = createStyledButton("Insert User", new Color(70, 130, 180));
            JButton btnInsertSong = createStyledButton("Insert Song", new Color(255, 165, 0));
            JButton btnInsertPlaylist = createStyledButton("Insert Playlist", new Color(34, 139, 34));
            JButton btnViewSongs = createStyledButton("View Songs", new Color(123, 104, 238));
            JButton btnViewConcerts = createStyledButton("View Concerts", new Color(72, 209, 204));
            JButton btnExit = createStyledButton("Exit", new Color(220, 20, 60));

            // Add action listeners
            btnInsertUser.addActionListener(new InsertUserListener());
            btnInsertSong.addActionListener(new InsertSongListener());
            btnInsertPlaylist.addActionListener(new InsertPlaylistListener());
            btnViewSongs.addActionListener(new ViewSongsListener());
            btnViewConcerts.addActionListener(new ViewConcertsListener());
            btnExit.addActionListener(e -> System.exit(0));

            // Add buttons to the panel
            panel.add(btnInsertUser);
            panel.add(btnInsertSong);
            panel.add(btnInsertPlaylist);
            panel.add(btnViewSongs);
            panel.add(btnViewConcerts);
            panel.add(btnExit);

            // Add panel to frame
            frame.add(panel, BorderLayout.CENTER);
            frame.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
        }
    }

    // Utility method to create styled buttons
    private static JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE); // Set text color to white for contrast
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Set button font size and style
        button.setFocusPainted(false); // Remove focus border on buttons
        return button;
    }

    // Utility method to display data in a table
    private static void displayTableData(String query, String title) {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Prepare table data
            String[] columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = metaData.getColumnLabel(i + 1);
            }

            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                model.addRow(row);
            }

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error displaying data: " + e.getMessage());
        }
    }

    // Listener for inserting a user
    static class InsertUserListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String idStr = JOptionPane.showInputDialog("Enter User ID:");
            String username = JOptionPane.showInputDialog("Enter Username:");
            String email = JOptionPane.showInputDialog("Enter Email:");
            String password = JOptionPane.showInputDialog("Enter Password:");

            if (idStr != null && username != null && email != null && password != null) {
                try {
                    int userId = Integer.parseInt(idStr);
                    String sql = "INSERT INTO users (user_id, username, email, created_at, password) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, userId);
                        pstmt.setString(2, username);
                        pstmt.setString(3, email);
                        pstmt.setTimestamp(4, Timestamp.from(Instant.now()));
                        pstmt.setString(5, password);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "User inserted successfully.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "User ID must be a valid number.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error inserting user: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please fill in all fields.");
            }
        }
    }

    // Listener for inserting a song
    static class InsertSongListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String songIdStr = JOptionPane.showInputDialog("Enter Song ID:");
            String title = JOptionPane.showInputDialog("Enter Song Title:");
            String durationStr = JOptionPane.showInputDialog("Enter Duration (in seconds):");
            String albumIdStr = JOptionPane.showInputDialog("Enter Album ID:");
            String artistIdStr = JOptionPane.showInputDialog("Enter Artist ID:");

            if (songIdStr != null && title != null && durationStr != null && albumIdStr != null && artistIdStr != null) {
                try {
                    int songId = Integer.parseInt(songIdStr);
                    int duration = Integer.parseInt(durationStr);
                    int albumId = Integer.parseInt(albumIdStr);
                    int artistId = Integer.parseInt(artistIdStr);

                    String sql = "INSERT INTO songs (song_id, title, duration, album_id, artist_id) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, songId);
                        pstmt.setString(2, title);
                        pstmt.setInt(3, duration);
                        pstmt.setInt(4, albumId);
                        pstmt.setInt(5, artistId);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Song inserted successfully.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "ID and duration fields must be valid numbers.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error inserting song: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please fill in all fields.");
            }
        }
    }

    // Listener for inserting a playlist
    static class InsertPlaylistListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String playlistIdStr = JOptionPane.showInputDialog("Enter Playlist ID:");
            String playlistName = JOptionPane.showInputDialog("Enter Playlist Name:");
            String userIdStr = JOptionPane.showInputDialog("Enter User ID:");

            if (playlistIdStr != null && playlistName != null && userIdStr != null) {
                try {
                    int playlistId = Integer.parseInt(playlistIdStr);
                    int userId = Integer.parseInt(userIdStr);

                    String sql = "INSERT INTO playlists (playlist_id, name, created_at, user_id) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, playlistId);
                        pstmt.setString(2, playlistName);
                        pstmt.setTimestamp(3, Timestamp.from(Instant.now()));
                        pstmt.setInt(4, userId);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Playlist inserted successfully.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Playlist ID and User ID must be valid numbers.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error inserting playlist: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please fill in all fields.");
            }
        }
    }

    // Listener for viewing songs
    static class ViewSongsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            displayTableData("SELECT * FROM songs", "Songs");
        }
    }

    // Listener for viewing concerts
    static class ViewConcertsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            displayTableData("SELECT * FROM concerts", "Concerts");
        }
    }
}
