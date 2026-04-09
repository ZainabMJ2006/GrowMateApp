package growmate;

import growmate.model.Plant;

import javax.swing.*;
import java.awt.*;

public class AddPlantDialog extends JDialog {
    public interface AddListener { void onAddPlant(Plant plant); }

    public AddPlantDialog(JFrame owner, AddListener listener) {
        super(owner, "Add New Plant", true);
        setSize(380, 300);
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,8,6,8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField name    = new JTextField(16);
        JTextField species = new JTextField(16);
        JSpinner  water    = new JSpinner(new SpinnerNumberModel(7, 1, 120, 1));
        JSpinner  fert     = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));
        JTextArea notes    = new JTextArea(3, 16);
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);

        JButton add = new JButton("Add");

        c.gridx=0; c.gridy=0; add(new JLabel("Name:"), c);
        c.gridx=1; c.gridy=0; add(name, c);

        c.gridx=0; c.gridy=1; add(new JLabel("Species:"), c);
        c.gridx=1; c.gridy=1; add(species, c);

        c.gridx=0; c.gridy=2; add(new JLabel("Water every (days):"), c);
        c.gridx=1; c.gridy=2; add(water, c);

        c.gridx=0; c.gridy=3; add(new JLabel("Fertilize every (days):"), c);
        c.gridx=1; c.gridy=3; add(fert, c);

        c.gridx=0; c.gridy=4; c.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("Notes / Reminder:"), c);
        c.gridx=1; c.gridy=4; add(new JScrollPane(notes), c);

        c.gridx=1; c.gridy=5; c.anchor = GridBagConstraints.CENTER;
        add(add, c);

        getRootPane().setDefaultButton(add);

        add.addActionListener(e -> {
            String n = name.getText().trim();
            if (n.isEmpty()) { JOptionPane.showMessageDialog(this, "Name is required."); return; }
            Plant p = new Plant(n, species.getText().trim(),
                    (Integer) water.getValue(),
                    (Integer) fert.getValue());
            p.setNotes(notes.getText().trim()); // <-- save your reminder
            listener.onAddPlant(p);
            dispose();
        });
    }
}
