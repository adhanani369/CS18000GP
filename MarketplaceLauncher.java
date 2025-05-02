import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Launcher for the Marketplace application.
 * This class serves as the entry point for the application and handles the initial splash screen.
 * @author Ayush Dhanani
 * @version
 */
public class MarketplaceLauncher {

    /**
     * Main method to start the application.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show splash screen
        SplashScreen splashScreen = new SplashScreen();
        splashScreen.setVisible(true);

        // Start application in a separate thread to allow splash screen to be shown
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Simulate loading time
                Thread.sleep(1500);
                return null;
            }

            @Override
            protected void done() {
                splashScreen.dispose();
                SwingUtilities.invokeLater(() -> new MarketPlaceGUI());
            }
        };
        worker.execute();
    }

    /**
     * Simple splash screen for the application.
     */
    private static class SplashScreen extends JWindow {
        public SplashScreen() {
            // Set size and center on screen
            setSize(500, 300);
            setLocationRelativeTo(null);

            // Create content panel with gradient background
            JPanel content = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    GradientPaint gp = new GradientPaint(0, 0, new Color(0, 102, 204),
                            w, h, new Color(0, 51, 102));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, w, h);
                }
            };
            content.setLayout(new BorderLayout());

            // Add title label
            JLabel titleLabel = new JLabel("Marketplace Application");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setHorizontalAlignment(JLabel.CENTER);
            content.add(titleLabel, BorderLayout.NORTH);

            // Add version and copyright label
            JLabel versionLabel = new JLabel("Version 1.0.0 - © 2025");
            versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            versionLabel.setForeground(Color.WHITE);
            versionLabel.setHorizontalAlignment(JLabel.CENTER);
            content.add(versionLabel, BorderLayout.SOUTH);

            // Add loading animation (simulated with progress bar)
            JProgressBar progress = new JProgressBar();
            progress.setIndeterminate(true);
            progress.setStringPainted(false);
            progress.setBorderPainted(false);
            progress.setForeground(Color.WHITE);
            progress.setBackground(new Color(0, 51, 102));

            JPanel progressPanel = new JPanel(new BorderLayout());
            progressPanel.setOpaque(false);
            progressPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
            progressPanel.add(progress, BorderLayout.SOUTH);

            // Add logo (simulated with text)
            JLabel logoLabel = new JLabel("MARKETPLACE");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 36));
            logoLabel.setForeground(Color.WHITE);
            logoLabel.setHorizontalAlignment(JLabel.CENTER);
            progressPanel.add(logoLabel, BorderLayout.CENTER);

            content.add(progressPanel, BorderLayout.CENTER);

            // Set content pane
            setContentPane(content);
        }
    }
}
