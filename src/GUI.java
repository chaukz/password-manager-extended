import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GUI {

    // ─── Color Palette ───────────────────────────────────────────────
    private static final Color BG = new Color(18, 18, 24);
    private static final Color PANEL = new Color(28, 28, 38);
    private static final Color CARD = new Color(38, 38, 52);
    private static final Color ACCENT = new Color(99, 102, 241);
    private static final Color ACCENT_HOV = new Color(79, 82, 221);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color TEXT = new Color(236, 236, 241);
    private static final Color TEXT_DIM = new Color(148, 148, 170);
    private static final Color BORDER = new Color(55, 55, 75);
    private static final Font FONT_MAIN = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    private PasswordManager manager;
    private JFrame frame;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;

    // ─── Entry Point ──────────────────────────────────────────────────
    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            GUI gui = new GUI();

            // ── FIX 7: First-run detection — show setup screen if no master
            // password is stored yet (old code had it hardcoded as a
            // constant, so new users could never set their own password).
            if (!DatabaseManager.masterPasswordExists()) {
                gui.showSetupScreen();
            } else {
                gui.showLoginScreen();
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════
    // SETUP SCREEN (first run only)
    // ══════════════════════════════════════════════════════════════════
    private void showSetupScreen() {
        frame = new JFrame("Password Manager — Setup");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 380);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG);
        root.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 0, 6, 0);
        g.gridx = 0;
        g.weightx = 1;

        JLabel icon = new JLabel("🔑", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        g.gridy = 0;
        root.add(icon, g);

        JLabel title = styledLabel("Welcome!", FONT_TITLE, TEXT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 1;
        root.add(title, g);

        JLabel sub = styledLabel("Set your master password to get started.", FONT_SMALL, TEXT_DIM);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 2;
        root.add(sub, g);

        JPasswordField passField = styledPasswordField("Choose master password");
        g.gridy = 3;
        g.insets = new Insets(14, 0, 6, 0);
        root.add(passField, g);

        JPasswordField confirmField = styledPasswordField("Confirm master password");
        g.gridy = 4;
        g.insets = new Insets(0, 0, 6, 0);
        root.add(confirmField, g);

        JLabel errorLabel = styledLabel("", FONT_SMALL, DANGER);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 5;
        root.add(errorLabel, g);

        JButton setBtn = styledButton("Set Password", ACCENT, ACCENT_HOV);
        g.gridy = 6;
        g.insets = new Insets(6, 0, 0, 0);
        root.add(setBtn, g);

        setBtn.addActionListener(e -> {
            String p1 = new String(passField.getPassword()).trim();
            String p2 = new String(confirmField.getPassword()).trim();
            if (p1.isEmpty()) {
                errorLabel.setText("Password cannot be empty.");
                return;
            }
            if (!p1.equals(p2)) {
                errorLabel.setText("Passwords do not match.");
                shakeComponent(frame);
                return;
            }
            // ── FIX 7 cont: store hash in DB, not hardcoded ──
            DatabaseManager.saveMasterPassword(EncryptionUtil.hashPassword(p1), "");
            frame.dispose();
            manager = new PasswordManager();
            showDashboard();
        });

        frame.setContentPane(root);
        frame.setVisible(true);
        passField.requestFocusInWindow();
    }

    // ══════════════════════════════════════════════════════════════════
    // LOGIN SCREEN
    // ══════════════════════════════════════════════════════════════════
    private void showLoginScreen() {
        frame = new JFrame("Password Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 340);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG);
        root.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 0, 6, 0);
        g.gridx = 0;
        g.weightx = 1;

        JLabel icon = new JLabel("🔐", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        g.gridy = 0;
        root.add(icon, g);

        JLabel title = styledLabel("Password Manager", FONT_TITLE, TEXT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 1;
        root.add(title, g);

        JLabel subtitle = styledLabel("Enter master password to continue", FONT_SMALL, TEXT_DIM);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 2;
        root.add(subtitle, g);

        JPasswordField passField = styledPasswordField("Master password");
        g.gridy = 3;
        g.insets = new Insets(14, 0, 6, 0);
        root.add(passField, g);

        JLabel errorLabel = styledLabel("", FONT_SMALL, DANGER);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 4;
        g.insets = new Insets(0, 0, 6, 0);
        root.add(errorLabel, g);

        JButton loginBtn = styledButton("Unlock", ACCENT, ACCENT_HOV);
        g.gridy = 5;
        g.insets = new Insets(6, 0, 0, 0);
        root.add(loginBtn, g);

        final int[] attempts = { 0 };
        final int MAX = 3;

        ActionListener loginAction = e -> {
            String input = new String(passField.getPassword());
            attempts[0]++;
            // ── FIX 7 cont: compare against DB hash, not hardcoded constant ──
            String storedHash = DatabaseManager.getMasterPasswordHash();
            if (storedHash != null && EncryptionUtil.hashPassword(input).equals(storedHash)) {
                manager = new PasswordManager();
                frame.dispose();
                showDashboard();
            } else {
                int left = MAX - attempts[0];
                if (left > 0) {
                    errorLabel.setText("Wrong password — " + left + " attempt(s) left");
                    shakeComponent(frame);
                } else {
                    errorLabel.setText("Too many attempts. Closing...");
                    loginBtn.setEnabled(false);
                    Timer t = new Timer(1500, ev -> System.exit(0));
                    t.setRepeats(false);
                    t.start();
                }
                passField.setText("");
            }
        };

        loginBtn.addActionListener(loginAction);
        passField.addActionListener(loginAction);

        frame.setContentPane(root);
        frame.setVisible(true);
        passField.requestFocusInWindow();
    }

    // ══════════════════════════════════════════════════════════════════
    // DASHBOARD
    // ══════════════════════════════════════════════════════════════════
    private void showDashboard() {
        frame = new JFrame("Password Manager — Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(820, 560);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PANEL);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)));

        JLabel appTitle = styledLabel("🔐 Password Manager", FONT_BOLD, TEXT);
        topBar.add(appTitle, BorderLayout.WEST);

        searchField = styledTextField("Search by website...");
        searchField.setPreferredSize(new Dimension(220, 34));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
        });
        topBar.add(searchField, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);

        String[] cols = { "Website", "Username", "Password" };
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BG);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 16));
        tablePanel.add(scroll, BorderLayout.CENTER);
        root.add(tablePanel, BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btnBar.setBackground(PANEL);
        btnBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        JButton addBtn = styledButton("+ Add", ACCENT, ACCENT_HOV);
        JButton updateBtn = styledButton("✏ Update", CARD, BORDER);
        JButton deleteBtn = styledButton("🗑 Delete", DANGER, new Color(200, 50, 50));
        JButton showBtn = styledButton("👁 Show Password", CARD, BORDER);

        btnBar.add(showBtn);
        btnBar.add(updateBtn);
        btnBar.add(deleteBtn);
        btnBar.add(addBtn);
        root.add(btnBar, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> showAddDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
        updateBtn.addActionListener(e -> showUpdateDialog());
        showBtn.addActionListener(e -> togglePasswordVisibility());

        frame.setContentPane(root);
        frame.setVisible(true);
        refreshTable();
    }

    // ══════════════════════════════════════════════════════════════════
    // DIALOGS
    // ══════════════════════════════════════════════════════════════════
    private void showAddDialog() {
        JDialog dialog = styledDialog("Add New Entry", 380, 280);
        JTextField webField = styledTextField("e.g. google.com");
        JTextField userField = styledTextField("e.g. john@email.com");
        JPasswordField passField = styledPasswordField("Password");

        JPanel form = formPanel(
                new String[] { "Website", "Username", "Password" },
                new JComponent[] { webField, userField, passField });

        JButton saveBtn = styledButton("Save Entry", ACCENT, ACCENT_HOV);
        JLabel errorLbl = styledLabel("", FONT_SMALL, DANGER);

        saveBtn.addActionListener(e -> {
            String w = webField.getText().trim();
            String u = userField.getText().trim();
            String p = new String(passField.getPassword()).trim();
            if (w.isEmpty() || u.isEmpty() || p.isEmpty()) {
                errorLbl.setText("All fields are required.");
                return;
            }
            if (manager.addEntry(new PasswordEntry(w, u, p))) {
                refreshTable();
                dialog.dispose();
                toast("Entry added!");
            } else {
                errorLbl.setText("Entry for '" + w + "' already exists.");
            }
        });

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(BG);
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        content.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(0, 6));
        bottom.setBackground(BG);
        bottom.add(errorLbl, BorderLayout.NORTH);
        bottom.add(saveBtn, BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private void showUpdateDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            toast("Select an entry first.");
            return;
        }

        String website = (String) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);

        JDialog dialog = styledDialog("Update Entry", 380, 240);
        JTextField userField = styledTextField(username);
        userField.setText(username);
        JPasswordField passField = styledPasswordField("New password");

        JPanel form = formPanel(
                new String[] { "Username", "New Password" },
                new JComponent[] { userField, passField });

        JButton saveBtn = styledButton("Update Entry", ACCENT, ACCENT_HOV);
        JLabel errorLbl = styledLabel("", FONT_SMALL, DANGER);

        saveBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword()).trim();
            if (u.isEmpty() || p.isEmpty()) {
                errorLbl.setText("Fields cannot be empty.");
                return;
            }
            manager.updateEntry(website, u, p);
            refreshTable();
            dialog.dispose();
            toast("Entry updated!");
        });

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(BG);
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel heading = styledLabel("Updating: " + website, FONT_BOLD, TEXT_DIM);
        content.add(heading, BorderLayout.NORTH);
        content.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(0, 6));
        bottom.setBackground(BG);
        bottom.add(errorLbl, BorderLayout.NORTH);
        bottom.add(saveBtn, BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            toast("Select a row first.");
            return;
        }
        String website = (String) tableModel.getValueAt(row, 0);
        if (website == null || website.isEmpty()) {
            toast("Invalid selection.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Delete entry for '" + website + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            manager.deleteEntry(website);
            // Clear selection before refresh so no stale row remains
            table.clearSelection();
            refreshTable();
            toast("Entry deleted.");
        }
    }

    private boolean passwordsVisible = false;

    private void togglePasswordVisibility() {
        passwordsVisible = !passwordsVisible;
        refreshTable();
    }

    // ══════════════════════════════════════════════════════════════════
    // TABLE HELPERS
    // ══════════════════════════════════════════════════════════════════
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (PasswordEntry e : manager.listEntries()) {
            String pass = passwordsVisible ? e.getPassword() : "••••••••";
            tableModel.addRow(new Object[] { e.getWebsite(), e.getUsername(), pass });
        }
    }

    private void filterTable() {
        String raw = searchField.getText();
        // Don't filter when placeholder text is showing
        if (raw.equals("Search by website...")) {
            refreshTable();
            return;
        }
        String query = raw.trim().toLowerCase();
        tableModel.setRowCount(0);
        for (PasswordEntry e : manager.listEntries()) {
            if (e.getWebsite().toLowerCase().contains(query)) {
                String pass = passwordsVisible ? e.getPassword() : "••••••••";
                tableModel.addRow(new Object[] { e.getWebsite(), e.getUsername(), pass });
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // STYLE HELPERS (unchanged)
    // ══════════════════════════════════════════════════════════════════
    private void styleTable(JTable t) {
        t.setBackground(CARD);
        t.setForeground(TEXT);
        t.setFont(FONT_MAIN);
        t.setRowHeight(36);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(99, 102, 241, 80));
        t.setSelectionForeground(TEXT);
        t.setFocusable(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = t.getTableHeader();
        header.setBackground(PANEL);
        header.setForeground(TEXT_DIM);
        header.setFont(FONT_BOLD);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
                setBackground(sel ? new Color(99, 102, 241, 60) : (r % 2 == 0 ? CARD : new Color(33, 33, 46)));
                setForeground(TEXT);
                return this;
            }
        };
        for (int i = 0; i < t.getColumnCount(); i++)
            t.getColumnModel().getColumn(i).setCellRenderer(renderer);
    }

    private JButton styledButton(String text, Color bg, Color hover) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(TEXT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    private JTextField styledTextField(String placeholder) {
        JTextField f = new JTextField();
        f.setBackground(CARD);
        f.setForeground(TEXT_DIM);
        f.setCaretColor(TEXT);
        f.setFont(FONT_MAIN);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.setText(placeholder);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setForeground(TEXT);
                }
            }

            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(placeholder);
                    f.setForeground(TEXT_DIM);
                }
            }
        });
        return f;
    }

    private JPasswordField styledPasswordField(String placeholder) {
        JPasswordField f = new JPasswordField();
        f.setBackground(CARD);
        f.setForeground(TEXT_DIM);
        f.setCaretColor(TEXT);
        f.setFont(FONT_MAIN);
        f.setEchoChar((char) 0);
        f.setText(placeholder);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (new String(f.getPassword()).equals(placeholder)) {
                    f.setText("");
                    f.setEchoChar('•');
                    f.setForeground(TEXT);
                }
            }

            public void focusLost(FocusEvent e) {
                if (f.getPassword().length == 0) {
                    f.setEchoChar((char) 0);
                    f.setText(placeholder);
                    f.setForeground(TEXT_DIM);
                }
            }
        });
        return f;
    }

    private JLabel styledLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    private JDialog styledDialog(String title, int w, int h) {
        JDialog d = new JDialog(frame, title, true);
        d.setSize(w, h);
        d.setLocationRelativeTo(frame);
        d.setResizable(false);
        d.getContentPane().setBackground(BG);
        return d;
    }

    private JPanel formPanel(String[] labels, JComponent[] fields) {
        JPanel p = new JPanel(new GridLayout(labels.length, 2, 10, 10));
        p.setBackground(BG);
        for (int i = 0; i < labels.length; i++) {
            p.add(styledLabel(labels[i], FONT_BOLD, TEXT_DIM));
            p.add(fields[i]);
        }
        return p;
    }

    private void toast(String message) {
        JWindow toast = new JWindow(frame);
        JLabel lbl = new JLabel(message, SwingConstants.CENTER);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(TEXT);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(50, 50, 70));
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        toast.setContentPane(lbl);
        toast.pack();
        Point loc = frame.getLocationOnScreen();
        toast.setLocation(loc.x + (frame.getWidth() - toast.getWidth()) / 2,
                loc.y + frame.getHeight() - 70);
        toast.setVisible(true);
        Timer t = new Timer(2000, e -> toast.dispose());
        t.setRepeats(false);
        t.start();
    }

    private void shakeComponent(JFrame f) {
        Point origin = f.getLocation();
        Timer t = new Timer(30, null);
        final int[] count = { 0 };
        t.addActionListener(e -> {
            int dx = (count[0] % 2 == 0) ? 8 : -8;
            f.setLocation(origin.x + dx, origin.y);
            if (++count[0] > 8) {
                t.stop();
                f.setLocation(origin);
            }
        });
        t.start();
    }

    // ── FIX 7: getStoredHash() removed — login now reads directly from DB ──
}