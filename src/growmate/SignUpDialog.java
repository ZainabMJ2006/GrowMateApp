package growmate;

import growmate.dao.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class SignUpDialog extends JDialog {
    private String lastCreatedUsername;
    public String getLastCreatedUsername(){ return lastCreatedUsername; }

    public SignUpDialog(JFrame owner) {
        super(owner, "Create Account", true);
        setSize(380, 260);
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,8,6,8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField user = new JTextField(16);
        JPasswordField pass = new JPasswordField(16);
        JPasswordField confirm = new JPasswordField(16);
        JButton create = new JButton("Sign Up");

        c.gridx=0; c.gridy=0; add(new JLabel("Username:"), c);
        c.gridx=1; c.gridy=0; add(user, c);
        c.gridx=0; c.gridy=1; add(new JLabel("Password:"), c);
        c.gridx=1; c.gridy=1; add(pass, c);
        c.gridx=0; c.gridy=2; add(new JLabel("Confirm Password:"), c);
        c.gridx=1; c.gridy=2; add(confirm, c);
        c.gridx=1; c.gridy=3; add(create, c);

        getRootPane().setDefaultButton(create);

        create.addActionListener(e -> {
            String u = user.getText().trim();
            String p1 = new String(pass.getPassword());
            String p2 = new String(confirm.getPassword());

            if (u.isEmpty() || p1.isEmpty() || p2.isEmpty()) { JOptionPane.showMessageDialog(this, "All fields are required."); return; }
            if (u.length() < 3 || u.contains(" ")) { JOptionPane.showMessageDialog(this, "Username must be ≥ 3 chars and no spaces."); return; }
            if (p1.length() < 4) { JOptionPane.showMessageDialog(this, "Password must be ≥ 4 chars."); return; }
            if (!p1.equals(p2)) { JOptionPane.showMessageDialog(this, "Passwords do not match."); return; }

            try {
                UserDAO dao = new UserDAO();
                if (dao.exists(u)) { JOptionPane.showMessageDialog(this, "Username already exists."); return; }
                if (dao.create(u, p1) != null) {
                    lastCreatedUsername = u;
                    JOptionPane.showMessageDialog(this, "Account created! You can log in now.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not create account.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
            }
        });
    }
}
