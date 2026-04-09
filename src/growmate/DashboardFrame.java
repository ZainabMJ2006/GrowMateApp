package growmate;

import growmate.dao.PlantDAO;
import growmate.model.Plant;
import growmate.model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class DashboardFrame extends JFrame {
    private final User user;
    private final PlantDAO plantDAO = new PlantDAO();
    private final DefaultListModel<Plant> model = new DefaultListModel<>();
    private final JList<Plant> list = new JList<>(model);

    public DashboardFrame(User user) {
        this.user = user;

        setTitle("GrowMate — Dashboard • " + user.getUsername());
        setSize(720, 460);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color lightGreen = new Color(218, 247, 166);
        Color darkGreen = new Color(56, 142, 60);
        Color btnGreen = new Color(102, 187, 106);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(darkGreen);
        JLabel title = new JLabel("  🌿 Your Plants");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.add(title, BorderLayout.WEST);

        // List
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> listComp, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(listComp, value, index, isSelected, cellHasFocus);
                if (value instanceof Plant) {
                    Plant p = (Plant) value;
                    label.setText(p.getName() + (p.getSpecies() == null || p.getSpecies().isBlank() ? "" : " (" + p.getSpecies() + ")"));
                    if (!isSelected) {
                        label.setOpaque(true);
                        label.setBackground(p.getStatusColor()); // yellow/green/red
                    }
                }
                return label;
            }
        });

        JScrollPane scroll = new JScrollPane(list);

        // Buttons
        JButton addBtn = styledButton("Add Plant", btnGreen);
        JButton waterBtn = styledButton("Water", btnGreen);
        JButton fertBtn = styledButton("Fertilize", btnGreen);
        JButton noteBtn = styledButton("View/Edit Reminder", btnGreen);
        JButton remBtn = styledButton("Check Reminders", btnGreen);
        JButton delBtn = styledButton("Delete Plant", new Color(244, 67, 54));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actions.setBackground(lightGreen);
        actions.add(addBtn);
        actions.add(waterBtn);
        actions.add(fertBtn);
        actions.add(noteBtn);
        actions.add(remBtn);
        actions.add(delBtn);

        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);

        // Load data
        reloadPlants();

        // Add plant
        addBtn.addActionListener(e -> {
            AddPlantDialog dlg = new AddPlantDialog(this, plant -> {
                plant.setUserId(user.getId());
                // no watering yet -> start as yellow
                plant.setLastWatered(null);
                try {
                    plantDAO.create(plant);
                    reloadPlants();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
                }
            });
            dlg.setVisible(true);
        });

        // Water / Fertilize
        waterBtn.addActionListener(e -> updateCare(true));
        fertBtn.addActionListener(e -> updateCare(false));

        // Edit reminder
        noteBtn.addActionListener(e -> {
            Plant p = list.getSelectedValue();
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Select a plant first.");
                return;
            }
            String current = p.getNotes() == null ? "" : p.getNotes();
            String updated = JOptionPane.showInputDialog(this, "Edit reminder for " + p.getName(), current);
            if (updated != null) {
                p.setNotes(updated);
                try {
                    plantDAO.updateNotes(p.getId(), updated, user.getId());
                    JOptionPane.showMessageDialog(this, "Reminder updated!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
                }
            }
        });

        // Check reminders
        remBtn.addActionListener(e -> showReminders());

        // Delete plant
        delBtn.addActionListener(e -> {
            Plant p = list.getSelectedValue();
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Select a plant to delete.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete " + p.getName() + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    plantDAO.delete(p.getId(), user.getId());
                    reloadPlants();
                    JOptionPane.showMessageDialog(this, p.getName() + " deleted successfully.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
                }
            }
        });
    }

    private JButton styledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        return b;
    }

    private void reloadPlants() {
        try {
            model.clear();
            List<Plant> listFromDb = plantDAO.listByUser(user.getId());
            for (Plant p : listFromDb) model.addElement(p);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    private void updateCare(boolean watering) {
        Plant p = list.getSelectedValue();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Select a plant first.");
            return;
        }
        if (watering) p.setLastWatered(LocalDate.now());
        else p.setLastFertilized(LocalDate.now());
        try {
            plantDAO.updateCareDates(p.getId(), p.getLastWatered(), p.getLastFertilized(), user.getId());
            reloadPlants();
            JOptionPane.showMessageDialog(this, (watering ? "Watered " : "Fertilized ") + p.getName() + "!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    // ✅ Fixed: no special characters or emojis
    private void showReminders() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < model.size(); i++) {
            Plant p = model.get(i);
            boolean any = false;

            if (p.needsWatering()) {
                sb.append("Water ").append(p.getName()).append("\n");
                any = true;
            }
            if (p.needsFertilizing()) {
                sb.append("Fertilize ").append(p.getName()).append("\n");
                any = true;
            }
            if (p.getNotes() != null && !p.getNotes().isBlank()) {
                sb.append("Note for ").append(p.getName())
                        .append(": ").append(p.getNotes()).append("\n");
                any = true;
            }
            if (any) sb.append("\n");
        }

        JOptionPane.showMessageDialog(this,
                sb.length() == 0 ? "All plants are up to date!" : sb.toString());
    }
}
