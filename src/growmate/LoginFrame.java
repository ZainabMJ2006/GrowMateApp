package growmate;

import growmate.dao.UserDAO;
import growmate.model.User;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("🌿 GrowMate Login");
        setSize(460, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Color lightGreen = new Color(218, 247, 166);
        Color darkGreen  = new Color(56, 142, 60);
        Color btnGreen   = new Color(102, 187, 106);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(lightGreen);

        // header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        header.setBackground(darkGreen);

        JLabel title = new JLabel("GrowMate");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel iconLabel = new JLabel("🌿");
        URL iconUrl = LoginFrame.class.getResource("/growmate/assets/plant.png");
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            Image scaled = icon.getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH);
            iconLabel = new JLabel(new ImageIcon(scaled));
        }
        header.add(iconLabel);
        header.add(title);

        // form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(lightGreen);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField userTxt = new JTextField(16);
        JPasswordField passTxt = new JPasswordField(16);

        c.gridx=0; c.gridy=0; form.add(new JLabel("Username:"), c);
        c.gridx=1; c.gridy=0; form.add(userTxt, c);
        c.gridx=0; c.gridy=1; form.add(new JLabel("Password:"), c);
        c.gridx=1; c.gridy=1; form.add(passTxt, c);

        // footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        footer.setBackground(lightGreen);
        JButton loginBtn = new JButton("Login");
        JButton signUpBtn = new JButton("Sign Up");
        for (JButton b : new JButton[]{loginBtn, signUpBtn}) {
            b.setBackground(btnGreen);
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setFont(new Font("Arial", Font.BOLD, 14));
            footer.add(b);
        }

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);
        getRootPane().setDefaultButton(loginBtn);

        // actions
        loginBtn.addActionListener(e -> {
            String username = userTxt.getText().trim();
            String password = new String(passTxt.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter username and password.");
                return;
            }
            try {
                UserDAO dao = new UserDAO();
                if (!dao.authenticate(username, password)) {
                    JOptionPane.showMessageDialog(this, "Incorrect username or password.");
                    return;
                }
                User user = dao.findByUsername(username);
                new DashboardFrame(user).setVisible(true);
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
            }
        });

        signUpBtn.addActionListener(e -> {
            SignUpDialog dlg = new SignUpDialog(this);
            dlg.setVisible(true);
            if (dlg.getLastCreatedUsername() != null) {
                userTxt.setText(dlg.getLastCreatedUsername());
                passTxt.setText("");
            }
        });
    }
}
