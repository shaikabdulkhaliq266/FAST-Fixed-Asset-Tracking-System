package main;

import database.DatabaseInitializer;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Asset;
import model.Assignment;
import model.Employee;
import service.AssetService;
import service.AssignmentService;
import service.EmployeeService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MainApp.java  —  Professional Edition
 * Fixed Asset Tracking System | JavaFX GUI
 *
 * Features:
 *  • Glassmorphism dark theme with gradient accents
 *  • Animated dashboard with live bar + donut charts
 *  • Full CRUD: Add / Edit / Delete for Assets & Employees
 *  • Maintenance status tracker with inline status changer
 *  • SQL-based summary report panel
 *  • Smooth page transitions and micro-animations
 *
 * Muffakham Jah College of Engineering and Technology
 * MJ-Industry Ready Program | Academic Year 2025-26
 */
public class MainApp extends Application {

    // ── Services ────────────────────────────────────────────────────────────
    private final AssetService      assetSvc  = new AssetService();
    private final EmployeeService   empSvc    = new EmployeeService();
    private final AssignmentService assignSvc = new AssignmentService();

    // ── Palette ─────────────────────────────────────────────────────────────
    private static final String C_BG       = "#0d0d1a";
    private static final String C_SURFACE  = "#13132b";
    private static final String C_CARD     = "#1a1a35";
    private static final String C_BORDER   = "#2a2a50";
    private static final String C_ACCENT   = "#7c6af7";
    private static final String C_ACCENT2  = "#f7456a";
    private static final String C_GREEN    = "#00e5a0";
    private static final String C_ORANGE   = "#ffaa00";
    private static final String C_PURPLE   = "#b44fff";
    private static final String C_TEXT     = "#e8e8ff";
    private static final String C_MUTED    = "#6b6b99";

    // ── Layout ───────────────────────────────────────────────────────────────
    private BorderPane  root;
    private StackPane   contentStack;
    private Button      activeNav;

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void start(Stage stage) {
        DatabaseInitializer.initialize();

        root = new BorderPane();
        root.setStyle("-fx-background-color: " + C_BG + ";");
        root.setLeft(buildSidebar());

        contentStack = new StackPane();
        contentStack.setStyle("-fx-background-color: " + C_BG + ";");
        root.setCenter(contentStack);

        showDashboard();

        Scene scene = new Scene(root, 1280, 780);
        stage.setTitle("FAST — Fixed Asset Tracking System");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.show();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SIDEBAR
    // ══════════════════════════════════════════════════════════════════════════
    private VBox buildSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.setPrefWidth(240);
        sidebar.setPadding(new Insets(28, 16, 28, 16));
        sidebar.setStyle(
            "-fx-background-color: " + C_SURFACE + ";" +
            "-fx-border-color: " + C_BORDER + ";" +
            "-fx-border-width: 0 1 0 0;");

        // Logo block
        VBox logo = new VBox(2);
        Label logoText = new Label("FAST");
        logoText.setFont(Font.font("Georgia", FontWeight.BOLD, 36));
        logoText.setStyle("-fx-text-fill: transparent;" +
            "-fx-background-color: linear-gradient(to right, " + C_ACCENT + ", " + C_PURPLE + ");" +
            "-fx-background-radius: 4;" +
            "-fx-padding: 0 4;");
        // fallback solid color for JavaFX (text-fill transparent doesn't work, use color)
        logoText.setTextFill(Color.web(C_ACCENT));
        logoText.setFont(Font.font("Georgia", FontWeight.BOLD, 36));

        Label logoSub = new Label("Fixed Asset Tracking");
        logoSub.setFont(Font.font("Verdana", 10));
        logoSub.setTextFill(Color.web(C_MUTED));
        logo.getChildren().addAll(logoText, logoSub);
        VBox.setMargin(logo, new Insets(0, 0, 24, 4));

        // Nav divider label
        Label navLabel = new Label("NAVIGATION");
        navLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        navLabel.setTextFill(Color.web(C_MUTED));
        VBox.setMargin(navLabel, new Insets(0, 0, 6, 8));

        Button btnDash    = navBtn("  Dashboard",    "🏠");
        Button btnAssets  = navBtn("  Assets",       "📦");
        Button btnEmp     = navBtn("  Employees",    "👥");
        Button btnAssign  = navBtn("  Assignments",  "🔗");
        Button btnMaint   = navBtn("  Maintenance",  "🔧");
        Button btnDepr    = navBtn("  Depreciation", "📉");
        Button btnDept    = navBtn("  Departments",  "🏢");
        Button btnReport  = navBtn("  SQL Report",   "📊");
        Button btnPdf     = navBtn("  PDF Report",   "🖨");
        Button btnExport  = navBtn("  Export .txt",  "📄");

        btnDash.setOnAction(e   -> navigate(btnDash,   this::showDashboard));
        btnAssets.setOnAction(e -> navigate(btnAssets, this::showAssets));
        btnEmp.setOnAction(e    -> navigate(btnEmp,    this::showEmployees));
        btnAssign.setOnAction(e -> navigate(btnAssign, this::showAssignments));
        btnMaint.setOnAction(e  -> navigate(btnMaint,  this::showMaintenance));
        btnDepr.setOnAction(e   -> navigate(btnDepr,   this::showDepreciation));
        btnDept.setOnAction(e   -> navigate(btnDept,   this::showDepartments));
        btnReport.setOnAction(e -> navigate(btnReport, this::showSQLReport));
        btnPdf.setOnAction(e    -> navigate(btnPdf,    this::showPdfReport));
        btnExport.setOnAction(e -> navigate(btnExport, this::exportReport));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Bottom info
        VBox bottomInfo = new VBox(4);
        bottomInfo.setPadding(new Insets(12));
        bottomInfo.setStyle("-fx-background-color: " + C_CARD + "; -fx-background-radius: 10;");
        Label college = new Label("Muffakham Jah College");
        college.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        college.setTextFill(Color.web(C_MUTED));
        Label program = new Label("MJ-IRP 2025–26  |  IV Sem");
        program.setFont(Font.font("Verdana", 9));
        program.setTextFill(Color.web(C_MUTED));
        bottomInfo.getChildren().addAll(college, program);

        sidebar.getChildren().addAll(
            logo, navLabel,
            btnDash, btnAssets, btnEmp, btnAssign, btnMaint,
            btnDepr, btnDept, btnReport, btnPdf, btnExport,
            spacer, bottomInfo
        );

        setNavActive(btnDash);
        return sidebar;
    }

    private Button navBtn(String text, String icon) {
        Button btn = new Button(icon + text);
        btn.setPrefWidth(208);
        btn.setPrefHeight(40);
        btn.setFont(Font.font("Verdana", 12));
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setCursor(javafx.scene.Cursor.HAND);
        styleNavInactive(btn);
        btn.setOnMouseEntered(e -> { if (btn != activeNav) styleNavHover(btn); });
        btn.setOnMouseExited(e  -> { if (btn != activeNav) styleNavInactive(btn); });
        return btn;
    }

    private void navigate(Button btn, Runnable show) {
        setNavActive(btn);
        // Fade transition
        FadeTransition ft = new FadeTransition(Duration.millis(180), contentStack);
        ft.setFromValue(1); ft.setToValue(0);
        ft.setOnFinished(e -> {
            show.run();
            FadeTransition ft2 = new FadeTransition(Duration.millis(200), contentStack);
            ft2.setFromValue(0); ft2.setToValue(1);
            ft2.play();
        });
        ft.play();
    }

    private void setNavActive(Button btn) {
        if (activeNav != null) styleNavInactive(activeNav);
        btn.setStyle(
            "-fx-background-color: linear-gradient(to right, " + C_ACCENT + "22, " + C_ACCENT + "44);" +
            "-fx-text-fill: " + C_TEXT + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + C_ACCENT + ";" +
            "-fx-border-width: 0 0 0 3;" +
            "-fx-padding: 0 0 0 13;" +
            "-fx-font-weight: bold;");
        activeNav = btn;
    }
    private void styleNavInactive(Button btn) {
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + C_MUTED + ";" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 0 0 0 16;");
    }
    private void styleNavHover(Button btn) {
        btn.setStyle(
            "-fx-background-color: " + C_CARD + ";" +
            "-fx-text-fill: " + C_TEXT + ";" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 0 0 0 16;");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DASHBOARD
    // ══════════════════════════════════════════════════════════════════════════
    private void showDashboard() {
        ScrollPane scroll = pageScroll();
        VBox page = new VBox(20);
        page.setPadding(new Insets(28, 32, 40, 32));
        page.setStyle("-fx-background-color: " + C_BG + ";");

        // ── Data ──────────────────────────────────────────────────────────────
        List<Asset>      assets  = assetSvc.getAllAssets();
        List<Employee>   emps    = empSvc.getAllEmployees();
        List<Assignment> assigns = assignSvc.getAllAssignments();
        List<Assignment> active  = assignSvc.getActiveAssignments();
        long avail   = assets.stream().filter(a -> a.getStatus().equals("Available")).count();
        long asgn    = assets.stream().filter(a -> a.getStatus().equals("Assigned")).count();
        long maint   = assets.stream().filter(a -> a.getStatus().equals("Maintenance")).count();
        long retired = assets.stream().filter(a -> a.getStatus().equals("Retired")).count();
        double totalVal = assets.stream().mapToDouble(Asset::getPurchasePrice).sum();
        double utilRate = assets.isEmpty() ? 0 : (double) asgn / assets.size() * 100;
        double avgVal   = assets.isEmpty() ? 0 : totalVal / assets.size();
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy  |  HH:mm"));

        // ── HERO BANNER ───────────────────────────────────────────────────────
        HBox hero = new HBox();
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.setPadding(new Insets(28, 36, 28, 36));
        hero.setStyle(
            "-fx-background-color: linear-gradient(to right, #1e1060, #0e2050, #0d0d1a);" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: " + C_ACCENT + "55;" +
            "-fx-border-radius: 18; -fx-border-width: 1;");
        DropShadow hs = new DropShadow(32, Color.web(C_ACCENT + "44"));
        hero.setEffect(hs);

        VBox heroLeft = new VBox(4);
        HBox.setHgrow(heroLeft, Priority.ALWAYS);
        Label heroTag = new Label("🏛  FIXED ASSET TRACKING  —  MJ-IRP 2025-26");
        heroTag.setFont(Font.font("Verdana", FontWeight.BOLD, 9));
        heroTag.setTextFill(Color.web(C_ACCENT));
        Label heroTitle = new Label("Asset Control Dashboard");
        heroTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 30));
        heroTitle.setTextFill(Color.web(C_TEXT));
        Label heroCollege = new Label("Muffakham Jah College of Engineering & Technology");
        heroCollege.setFont(Font.font("Verdana", 11));
        heroCollege.setTextFill(Color.web("#8888bb"));
        heroLeft.getChildren().addAll(heroTag, heroTitle, heroCollege);

        // Vertical divider
        Region divider = new Region();
        divider.setPrefWidth(2); divider.setPrefHeight(64);
        divider.setStyle("-fx-background-color: linear-gradient(to bottom," + C_ACCENT + "," + C_PURPLE + ");" +
            "-fx-background-radius: 2;");
        HBox.setMargin(divider, new Insets(0, 32, 0, 32));

        VBox heroRight = new VBox(4);
        heroRight.setAlignment(Pos.CENTER_RIGHT);
        Label heroTimeLabel = new Label(ts);
        heroTimeLabel.setFont(Font.font("Verdana", 11));
        heroTimeLabel.setTextFill(Color.web(C_MUTED));
        Label heroPortfolio = new Label("Rs. " + String.format("%,.0f", totalVal));
        heroPortfolio.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
        heroPortfolio.setTextFill(Color.web(C_GREEN));
        Label heroPortfolioLbl = new Label("💰  Total Portfolio Value");
        heroPortfolioLbl.setFont(Font.font("Verdana", 10));
        heroPortfolioLbl.setTextFill(Color.web(C_MUTED));
        heroRight.getChildren().addAll(heroTimeLabel, heroPortfolio, heroPortfolioLbl);
        hero.getChildren().addAll(heroLeft, divider, heroRight);

        // ── KPI CARDS ─────────────────────────────────────────────────────────
        HBox kpiRow = new HBox(14);
        kpiRow.getChildren().addAll(
            dashKpi("Total Assets",  assets.size(), C_ACCENT,  "All registered",  "📦 ASSETS"),
            dashKpi("Available",     (int)avail,    C_GREEN,   "Ready to deploy", "✅ FREE"),
            dashKpi("Assigned",      (int)asgn,     C_ACCENT2, "In use",          "🔗 IN USE"),
            dashKpi("Maintenance",   (int)maint,    C_ORANGE,  "Under service",   "🔧 SERVICE"),
            dashKpi("Employees",     emps.size(),   C_PURPLE,  "Staff members",   "👥 STAFF")
        );

        // ── CHARTS ROW ────────────────────────────────────────────────────────
        HBox chartsRow = new HBox(16);

        // Bar chart
        VBox barCard = dashCard("📊  Status Distribution");
        HBox.setHgrow(barCard, Priority.ALWAYS);
        Canvas barCanvas = new Canvas(520, 210);
        drawProfessionalBar(barCanvas, avail, asgn, maint, retired);
        barCard.getChildren().add(barCanvas);

        // Donut chart
        VBox donutCard = dashCard("🎯  Breakdown");
        donutCard.setPrefWidth(300);
        Canvas donutCanvas = new Canvas(268, 230);
        drawProfessionalDonut(donutCanvas, avail, asgn, maint, retired);
        donutCard.getChildren().add(donutCanvas);

        chartsRow.getChildren().addAll(barCard, donutCard);

        // ── LOWER ROW: Category Progress + Quick Stats ────────────────────────
        HBox lowerRow = new HBox(16);

        // Category horizontal progress bars
        VBox catCard = dashCard("📦  By Category");
        HBox.setHgrow(catCard, Priority.ALWAYS);
        VBox catList = new VBox(11);
        String[] catColors = {C_ACCENT, C_GREEN, C_ACCENT2, C_ORANGE, C_PURPLE};
        Map<String, Long> catMap = assets.stream()
            .collect(Collectors.groupingBy(Asset::getCategory, Collectors.counting()));
        int ci = 0;
        for (Map.Entry<String, Long> entry : catMap.entrySet()) {
            double pct = assets.isEmpty() ? 0 : (double) entry.getValue() / assets.size();
            String col = catColors[ci % catColors.length];
            HBox catRow = new HBox(12);
            catRow.setAlignment(Pos.CENTER_LEFT);
            // Color tag
            Region tag = new Region();
            tag.setPrefWidth(4); tag.setPrefHeight(18);
            tag.setStyle("-fx-background-color:" + col + "; -fx-background-radius:2;");
            Label nameLbl = new Label(entry.getKey());
            nameLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
            nameLbl.setTextFill(Color.web(C_TEXT));
            nameLbl.setPrefWidth(105);
            // Progress track
            StackPane track = new StackPane();
            track.setPrefHeight(8);
            HBox.setHgrow(track, Priority.ALWAYS);
            Region trackBg = new Region();
            trackBg.setPrefHeight(8);
            trackBg.setMaxWidth(Double.MAX_VALUE);
            trackBg.setStyle("-fx-background-color:" + C_SURFACE + "; -fx-background-radius:4;");
            Region trackFill = new Region();
            trackFill.setPrefHeight(8);
            trackFill.setStyle("-fx-background-color:linear-gradient(to right," + col + "99," + col + ");" +
                "-fx-background-radius:4;");
            track.getChildren().addAll(trackBg, trackFill);
            StackPane.setAlignment(trackFill, Pos.CENTER_LEFT);
            // Animate width via property
            final double finalPct = pct;
            trackFill.setPrefWidth(0);
            Timeline barAnim = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(trackFill.prefWidthProperty(), 0)),
                new KeyFrame(Duration.millis(700 + ci * 100),
                    new KeyValue(trackFill.prefWidthProperty(), finalPct * 220)));
            barAnim.setDelay(Duration.millis(400));
            barAnim.play();
            Label pctLbl = new Label(String.format("%.0f%%", pct * 100) + "  (" + entry.getValue() + ")");
            pctLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
            pctLbl.setTextFill(Color.web(col));
            pctLbl.setPrefWidth(72);
            catRow.getChildren().addAll(tag, nameLbl, track, pctLbl);
            catList.getChildren().add(catRow);
            ci++;
        }
        if (catMap.isEmpty()) {
            Label noData = new Label("No assets yet. Add assets to see breakdown.");
            noData.setFont(Font.font("Verdana", 12));
            noData.setTextFill(Color.web(C_MUTED));
            catList.getChildren().add(noData);
        }
        catCard.getChildren().add(catList);

        // Quick stats
        VBox statsCard = dashCard("⚡  Key Metrics");
        statsCard.setPrefWidth(290);
        VBox statsList = new VBox(8);
        double maintRate = assets.isEmpty() ? 0 : (double) maint / assets.size() * 100;
        statsList.getChildren().addAll(
            statRow("Utilization",    String.format("%.1f%%", utilRate),              C_GREEN),
            statRow("Maintenance",    String.format("%.1f%%", maintRate),             C_ORANGE),
            statRow("Avg. Value",     "Rs. " + String.format("%,.0f", avgVal),        C_ACCENT),
            statRow("Assignments",    String.valueOf(assigns.size()),                  C_PURPLE),
            statRow("Active Now",     String.valueOf(active.size()),                   C_ACCENT2),
            statRow("Retired",        String.valueOf(retired),                         C_MUTED)
        );
        statsCard.getChildren().add(statsList);
        lowerRow.getChildren().addAll(catCard, statsCard);

        // ── RECENT ASSIGNMENTS ────────────────────────────────────────────────
        VBox recentCard = dashCard("🔗  Active Assignments");
        TableView<Assignment> recentTbl = buildTable();
        recentTbl.setPrefHeight(195);

        TableColumn<Assignment, String> rc1 = new TableColumn<>("ID");
        TableColumn<Assignment, String> rc2 = new TableColumn<>("Asset Name");
        TableColumn<Assignment, String> rc3 = new TableColumn<>("Assigned To");
        TableColumn<Assignment, String> rc4 = new TableColumn<>("Department");
        TableColumn<Assignment, String> rc5 = new TableColumn<>("Date Assigned");
        TableColumn<Assignment, String> rc6 = new TableColumn<>("Status");
        rc1.setPrefWidth(70); rc2.setPrefWidth(190); rc3.setPrefWidth(170);
        rc4.setPrefWidth(160); rc5.setPrefWidth(130); rc6.setPrefWidth(90);

        rc1.setCellValueFactory(d -> new SimpleStringProperty(
            "#" + String.format("%04d", d.getValue().getAssignmentId())));
        rc2.setCellValueFactory(d -> new SimpleStringProperty(
            assets.stream().filter(a -> a.getAssetId() == d.getValue().getAssetId())
                .map(Asset::getAssetName).findFirst().orElse("—")));
        rc3.setCellValueFactory(d -> new SimpleStringProperty(
            emps.stream().filter(e -> e.getEmployeeId() == d.getValue().getEmployeeId())
                .map(Employee::getEmployeeName).findFirst().orElse("—")));
        rc4.setCellValueFactory(d -> new SimpleStringProperty(
            emps.stream().filter(e -> e.getEmployeeId() == d.getValue().getEmployeeId())
                .map(Employee::getDepartment).findFirst().orElse("—")));
        rc5.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAssignedDate()));
        rc6.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        rc6.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("-fx-text-fill:" + C_ACCENT2 + "; -fx-font-weight:bold;" +
                    "-fx-background-color:" + C_ACCENT2 + "18; -fx-background-radius:4;");
            }
        });
        recentTbl.getColumns().addAll(rc1, rc2, rc3, rc4, rc5, rc6);
        recentTbl.getItems().setAll(active);
        Label emptyLbl = new Label("No active assignments  —  all assets are available.");
        emptyLbl.setFont(Font.font("Verdana", 12));
        emptyLbl.setTextFill(Color.web(C_MUTED));
        recentTbl.setPlaceholder(emptyLbl);
        recentCard.getChildren().add(recentTbl);

        // ── ASSEMBLE WITH STAGGER ─────────────────────────────────────────────
        page.getChildren().addAll(hero, kpiRow, chartsRow, lowerRow, recentCard);
        javafx.scene.Node[] sections = {hero, kpiRow, chartsRow, lowerRow, recentCard};
        for (int i = 0; i < sections.length; i++) {
            sections[i].setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.millis(380), sections[i]);
            ft.setFromValue(0); ft.setToValue(1);
            ft.setDelay(Duration.millis(i * 100));
            TranslateTransition tt = new TranslateTransition(Duration.millis(380), sections[i]);
            tt.setFromY(14); tt.setToY(0);
            tt.setDelay(Duration.millis(i * 100));
            new ParallelTransition(ft, tt).play();
        }
        scroll.setContent(page);
        setContent(scroll);
    }

    /** Clean KPI card — no emojis, pure text design */
    private VBox dashKpi(String title, int value, String color, String sub, String tag) {
        VBox card = new VBox(0);
        card.setPrefWidth(192);
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom," + color + "18, " + C_CARD + ");" +
            "-fx-background-radius:16;" +
            "-fx-border-color:" + color + "66;" +
            "-fx-border-radius:16; -fx-border-width:1;");
        card.setEffect(new DropShadow(20, Color.web(color + "44")));

        // Bold top color strip
        Region strip = new Region();
        strip.setPrefHeight(5);
        strip.setMaxWidth(Double.MAX_VALUE);
        strip.setStyle("-fx-background-color:linear-gradient(to right," + color + "," + color + "55);" +
            "-fx-background-radius:16 16 0 0;");

        VBox inner = new VBox(5);
        inner.setPadding(new Insets(14, 18, 16, 18));

        Label tagLbl = new Label(tag);
        tagLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 8));
        tagLbl.setTextFill(Color.web(color));
        tagLbl.setStyle("-fx-background-color:" + color + "28; -fx-background-radius:4; -fx-padding:2 7;");

        Label val = new Label("0");
        val.setFont(Font.font("Georgia", FontWeight.BOLD, 36));
        val.setTextFill(Color.web(color));

        Label ttl = new Label(title);
        ttl.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        ttl.setTextFill(Color.web(C_TEXT));

        Label s = new Label(sub);
        s.setFont(Font.font("Verdana", 9));
        s.setTextFill(Color.web(C_MUTED));

        inner.getChildren().addAll(tagLbl, val, ttl, s);
        card.getChildren().addAll(strip, inner);

        // Count-up animation
        IntegerProperty counter = new SimpleIntegerProperty(0);
        counter.addListener((obs, ov, nv) -> val.setText(String.valueOf(nv.intValue())));
        new Timeline(
            new KeyFrame(Duration.ZERO,       new KeyValue(counter, 0)),
            new KeyFrame(Duration.millis(800), new KeyValue(counter, value))
        ).play();

        // Hover glow + lift
        card.setOnMouseEntered(e -> {
            card.setEffect(new DropShadow(30, Color.web(color + "88")));
            ScaleTransition stIn = new ScaleTransition(Duration.millis(130), card);
            stIn.setToX(1.04); stIn.setToY(1.04); stIn.play();
        });
        card.setOnMouseExited(e -> {
            card.setEffect(new DropShadow(20, Color.web(color + "44")));
            ScaleTransition stOut = new ScaleTransition(Duration.millis(130), card);
            stOut.setToX(1.0); stOut.setToY(1.0); stOut.play();
        });
        return card;
    }

    /** Card used only on dashboard — cleaner than sectionCard */
    private VBox dashCard(String title) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18, 22, 18, 22));
        card.setStyle(
            "-fx-background-color:" + C_CARD + ";" +
            "-fx-background-radius:16;" +
            "-fx-border-color:" + C_BORDER + ";" +
            "-fx-border-radius:16; -fx-border-width:1;");
        card.setEffect(new DropShadow(20, Color.web("#00000066")));
        Label lbl = new Label(title);
        lbl.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web(C_TEXT));
        // Gradient underline
        Region underline = new Region();
        underline.setPrefHeight(3); underline.setPrefWidth(48);
        underline.setStyle(
            "-fx-background-color:linear-gradient(to right," + C_ACCENT + "," + C_PURPLE + ");" +
            "-fx-background-radius:2;");
        card.getChildren().addAll(lbl, underline);
        return card;
    }

    /** Stat row for Quick Stats panel */
    private HBox statRow(String label, String value, String color) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(9, 12, 9, 12));
        row.setStyle("-fx-background-color:" + C_SURFACE + "; -fx-background-radius:8;");
        // Left accent dot
        Region dot = new Region();
        dot.setPrefWidth(6); dot.setPrefHeight(6);
        dot.setStyle("-fx-background-color:" + color + "; -fx-background-radius:3;");
        HBox.setMargin(dot, new Insets(0, 10, 0, 0));
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Verdana", 11));
        lbl.setTextFill(Color.web(C_MUTED));
        HBox.setHgrow(lbl, Priority.ALWAYS);
        Label val = new Label(value);
        val.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        val.setTextFill(Color.web(color));
        row.getChildren().addAll(dot, lbl, val);
        return row;
    }


    /** Rich animated KPI card with percentage bar */
    private VBox richKpiCard(String title, int value, String icon,
                              String color, String sub, boolean hasData) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20, 22, 18, 22));
        card.setPrefWidth(192);
        card.setStyle(
            "-fx-background-color: " + C_CARD + ";" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: " + color + "33;" +
            "-fx-border-radius: 16; -fx-border-width: 1;");
        DropShadow ds = new DropShadow(16, Color.web(color + "22"));
        card.setEffect(ds);

        // Top row: icon + trend dot
        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label ico = new Label(icon);
        ico.setFont(Font.font(18));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label dot = new Label("●");
        dot.setFont(Font.font(8));
        dot.setTextFill(hasData ? Color.web(C_GREEN) : Color.web(C_MUTED));
        top.getChildren().addAll(ico, sp, dot);

        // Value with count-up
        Label val = new Label("0");
        val.setFont(Font.font("Georgia", FontWeight.BOLD, 34));
        val.setTextFill(Color.web(color));

        Label ttl = new Label(title);
        ttl.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        ttl.setTextFill(Color.web(C_TEXT));

        Label s = new Label(sub);
        s.setFont(Font.font("Verdana", 9));
        s.setTextFill(Color.web(C_MUTED));

        // Bottom accent bar
        Region bar = new Region();
        bar.setPrefHeight(3);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle("-fx-background-color: linear-gradient(to right," + color + "," + color + "33);" +
            "-fx-background-radius: 2;");

        card.getChildren().addAll(top, val, ttl, s, bar);

        // Count-up animation
        IntegerProperty counter = new SimpleIntegerProperty(0);
        counter.addListener((obs, ov, nv) -> val.setText(String.valueOf(nv.intValue())));
        Timeline tl = new Timeline(
            new KeyFrame(Duration.ZERO,        new KeyValue(counter, 0)),
            new KeyFrame(Duration.millis(700),  new KeyValue(counter, value))
        );
        tl.setDelay(Duration.millis(300));
        tl.play();

        // Hover lift effect
        card.setOnMouseEntered(e -> {
            card.setStyle(card.getStyle().replace(C_CARD,
                "-fx-background-color: " + C_CARD));
            ScaleTransition st = new ScaleTransition(Duration.millis(150), card);
            st.setToX(1.03); st.setToY(1.03); st.play();
        });
        card.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), card);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });

        return card;
    }

    /** Glassmorphism-style section card for dashboard */
    private VBox glassCard(String title) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20, 22, 20, 22));
        card.setStyle(
            "-fx-background-color: " + C_CARD + ";" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: " + C_BORDER + ";" +
            "-fx-border-radius: 16; -fx-border-width: 1;");
        DropShadow ds = new DropShadow(18, Color.web("#00000060"));
        card.setEffect(ds);
        Label lbl = new Label(title);
        lbl.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web(C_TEXT));
        card.getChildren().add(lbl);
        return card;
    }

    /** Quick stat row: label + value */
    private HBox quickStat(String label, String value, String color) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle("-fx-background-color:" + C_SURFACE + "; -fx-background-radius:8;");
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Verdana", 11));
        lbl.setTextFill(Color.web(C_MUTED));
        HBox.setHgrow(lbl, Priority.ALWAYS);
        Label val = new Label(value);
        val.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        val.setTextFill(Color.web(color));
        row.getChildren().addAll(lbl, val);
        return row;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ASSETS  (Add + Edit + Delete + Filter)
    // ══════════════════════════════════════════════════════════════════════════
    private void showAssets() {
        ScrollPane scroll = pageScroll();
        VBox page = pagePad();
        page.getChildren().add(pageHeader("Asset Management", "Add, edit, delete and filter assets"));

        // ── Add / Edit form ──────────────────────────────────────────────────
        VBox formCard = sectionCard("Add / Edit Asset");
        GridPane form = buildGrid();

        TextField tfName   = field("Asset Name",      "e.g. Dell Laptop XPS");
        TextField tfSerial = field("Serial Number",   "e.g. SN-2024-001");
        TextField tfCat    = field("Category",        "e.g. Electronics");
        TextField tfPrice  = field("Purchase Price",  "e.g. 55000.00");
        ComboBox<String> cbStatus = styledCombo("Available", "Assigned", "Maintenance", "Retired");

        // Hidden ID for edit mode
        final int[] editId = {-1};
        Label editBadge = new Label("✏ Edit Mode — Asset ID: ?");
        editBadge.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        editBadge.setTextFill(Color.web(C_ORANGE));
        editBadge.setVisible(false);
        editBadge.setManaged(false);

        form.add(flabel("Asset Name"),    0, 0); form.add(tfName,   1, 0);
        form.add(flabel("Serial Number"), 0, 1); form.add(tfSerial, 1, 1);
        form.add(flabel("Category"),      0, 2); form.add(tfCat,    1, 2);
        form.add(flabel("Purchase Price"),0, 3); form.add(tfPrice,  1, 3);
        form.add(flabel("Status"),        0, 4); form.add(cbStatus, 1, 4);
        form.add(editBadge,               1, 5);

        Button btnSave   = accentBtn("＋ Save Asset");
        Button btnCancel = ghostBtn("✕ Cancel Edit");
        btnCancel.setVisible(false); btnCancel.setManaged(false);
        HBox formBtns = new HBox(10, btnSave, btnCancel);
        form.add(formBtns, 1, 6);

        Label formMsg = new Label("");
        formMsg.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        form.add(formMsg, 1, 7);

        formCard.getChildren().add(form);

        // ── Filters ──────────────────────────────────────────────────────────
        VBox tableCard = sectionCard("Asset Inventory");
        HBox filterRow = new HBox(12);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        TextField tfSearch   = searchField("Search name or serial...");
        tfSearch.setPrefWidth(200);
        TextField tfIdSearch = searchField("Search by ID...");
        tfIdSearch.setPrefWidth(120);
        ComboBox<String> cbFStatus = styledCombo("All", "Available", "Assigned", "Maintenance", "Retired");
        ComboBox<String> cbFCat    = styledCombo("All", "Electronics", "Furniture", "Vehicles", "Machinery", "Network");
        Button btnFilter  = accentBtn("Filter");
        Button btnRefresh = ghostBtn("↺ Reset");
        filterRow.getChildren().addAll(tfIdSearch, tfSearch, cbFStatus, cbFCat, btnFilter, btnRefresh);

        // ── Table ─────────────────────────────────────────────────────────────
        TableView<Asset> table = buildTable();
        TableColumn<Asset, Integer> cId     = new TableColumn<>("ID");     cId.setPrefWidth(55);
        TableColumn<Asset, String>  cName   = new TableColumn<>("Asset Name"); cName.setPrefWidth(190);
        TableColumn<Asset, String>  cSerial = new TableColumn<>("Serial Number"); cSerial.setPrefWidth(150);
        TableColumn<Asset, String>  cCat    = new TableColumn<>("Category"); cCat.setPrefWidth(130);
        TableColumn<Asset, Double>  cPrice  = new TableColumn<>("Price (₹)"); cPrice.setPrefWidth(120);
        TableColumn<Asset, String>  cStatus = new TableColumn<>("Status"); cStatus.setPrefWidth(120);

        cId.setCellValueFactory(d     -> new SimpleIntegerProperty(d.getValue().getAssetId()).asObject());
        cName.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getAssetName()));
        cSerial.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSerialNumber()));
        cCat.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue().getCategory()));
        cPrice.setCellValueFactory(d  -> new SimpleDoubleProperty(d.getValue().getPurchasePrice()).asObject());
        cStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        statusColorCell(cStatus);

        table.getColumns().addAll(cId, cName, cSerial, cCat, cPrice, cStatus);
        table.getItems().setAll(assetSvc.getAllAssets());

        // ── Action bar ────────────────────────────────────────────────────────
        HBox actionBar = new HBox(10);
        Button btnEdit   = warningBtn("✏ Edit Selected");
        Button btnDelete = dangerBtn("🗑 Delete Selected");
        actionBar.getChildren().addAll(btnEdit, btnDelete);

        tableCard.getChildren().addAll(filterRow, table, actionBar);

        // ── Events ────────────────────────────────────────────────────────────
        Runnable refreshTable = () -> table.getItems().setAll(assetSvc.getAllAssets());

        btnSave.setOnAction(e -> {
            String name = tfName.getText().trim(), serial = tfSerial.getText().trim(),
                   cat  = tfCat.getText().trim(),  priceStr = tfPrice.getText().trim();
            if (name.isEmpty() || serial.isEmpty() || cat.isEmpty() || priceStr.isEmpty()) {
                setMsg(formMsg, "⚠ All fields are required.", C_ORANGE); return;
            }
            try {
                double price = Double.parseDouble(priceStr);
                if (price < 0) { setMsg(formMsg, "⚠ Price cannot be negative.", C_ORANGE); return; }

                if (editId[0] == -1) {
                    // ADD
                    boolean ok = assetSvc.addAsset(new Asset(0, name, serial, cat, price, cbStatus.getValue()));
                    if (ok) { setMsg(formMsg, "✅ Asset added!", C_GREEN); clearFields(tfName, tfSerial, tfCat, tfPrice); }
                    else    { setMsg(formMsg, "❌ Serial number already exists!", C_ACCENT2); }
                } else {
                    // UPDATE
                    assetSvc.updateAsset(editId[0], name, cat, price, cbStatus.getValue());
                    setMsg(formMsg, "✅ Asset updated!", C_GREEN);
                    editId[0] = -1;
                    editBadge.setVisible(false); editBadge.setManaged(false);
                    btnCancel.setVisible(false); btnCancel.setManaged(false);
                    btnSave.setText("＋ Save Asset");
                    clearFields(tfName, tfSerial, tfCat, tfPrice);
                }
                refreshTable.run();
            } catch (NumberFormatException ex) {
                setMsg(formMsg, "⚠ Price must be a valid number.", C_ORANGE);
            }
        });

        btnEdit.setOnAction(e -> {
            Asset sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Please select an asset to edit."); return; }
            tfName.setText(sel.getAssetName());
            tfSerial.setText(sel.getSerialNumber());
            tfCat.setText(sel.getCategory());
            tfPrice.setText(String.valueOf(sel.getPurchasePrice()));
            cbStatus.setValue(sel.getStatus());
            editId[0] = sel.getAssetId();
            editBadge.setText("✏ Edit Mode — Asset ID: " + sel.getAssetId());
            editBadge.setVisible(true); editBadge.setManaged(true);
            btnCancel.setVisible(true); btnCancel.setManaged(true);
            btnSave.setText("💾 Update Asset");
            setMsg(formMsg, "", C_TEXT);
        });

        btnCancel.setOnAction(e -> {
            editId[0] = -1;
            clearFields(tfName, tfSerial, tfCat, tfPrice);
            cbStatus.setValue("Available");
            editBadge.setVisible(false); editBadge.setManaged(false);
            btnCancel.setVisible(false); btnCancel.setManaged(false);
            btnSave.setText("＋ Save Asset");
            setMsg(formMsg, "", C_TEXT);
        });

        btnDelete.setOnAction(e -> {
            Asset sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Please select an asset to delete."); return; }
            assetSvc.deleteAsset(sel.getAssetId());
            refreshTable.run();
        });

        btnFilter.setOnAction(e -> {
            String kw   = tfSearch.getText().trim();
            String idKw = tfIdSearch.getText().trim();
            String st   = cbFStatus.getValue();
            String ct   = cbFCat.getValue();
            List<Asset> r = assetSvc.getAllAssets();
            if (!idKw.isEmpty()) {
                try {
                    int searchId = Integer.parseInt(idKw);
                    r = r.stream().filter(a -> a.getAssetId() == searchId).toList();
                } catch (NumberFormatException ex) {
                    setMsg(new Label(""), "⚠ ID must be a number.", C_ORANGE);
                }
            }
            if (!kw.isEmpty()) r = r.stream().filter(a ->
                a.getAssetName().toLowerCase().contains(kw.toLowerCase()) ||
                a.getSerialNumber().toLowerCase().contains(kw.toLowerCase())).toList();
            if (!"All".equals(st)) r = r.stream().filter(a -> a.getStatus().equals(st)).toList();
            if (!"All".equals(ct)) r = r.stream().filter(a -> a.getCategory().equalsIgnoreCase(ct)).toList();
            table.getItems().setAll(r);
        });

        btnRefresh.setOnAction(e -> {
            tfSearch.clear(); tfIdSearch.clear(); cbFStatus.setValue("All"); cbFCat.setValue("All");
            refreshTable.run();
        });

        page.getChildren().addAll(formCard, tableCard);
        scroll.setContent(page);
        setContent(scroll);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  EMPLOYEES  (Add + Edit + Delete + Search)
    // ══════════════════════════════════════════════════════════════════════════
    private void showEmployees() {
        ScrollPane scroll = pageScroll();
        VBox page = pagePad();
        page.getChildren().add(pageHeader("Employee Management", "Manage staff and their departments"));

        // Form
        VBox formCard = sectionCard("Add / Edit Employee");
        GridPane form = buildGrid();
        TextField tfName  = field("Name",       "e.g. Rahul Sharma");
        TextField tfDept  = field("Department", "e.g. IT Department");
        TextField tfEmail = field("Email",      "e.g. rahul@company.com");

        final int[] editId = {-1};
        Label editBadge = new Label("✏ Edit Mode — Employee ID: ?");
        editBadge.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        editBadge.setTextFill(Color.web(C_ORANGE));
        editBadge.setVisible(false); editBadge.setManaged(false);

        form.add(flabel("Full Name"),   0, 0); form.add(tfName,  1, 0);
        form.add(flabel("Department"),  0, 1); form.add(tfDept,  1, 1);
        form.add(flabel("Email"),       0, 2); form.add(tfEmail, 1, 2);
        form.add(editBadge,             1, 3);

        Button btnSave   = accentBtn("＋ Save Employee");
        Button btnCancel = ghostBtn("✕ Cancel Edit");
        btnCancel.setVisible(false); btnCancel.setManaged(false);
        HBox formBtns = new HBox(10, btnSave, btnCancel);
        form.add(formBtns, 1, 4);

        Label formMsg = new Label("");
        formMsg.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        form.add(formMsg, 1, 5);
        formCard.getChildren().add(form);

        // Table
        VBox tableCard = sectionCard("Employee Directory");
        HBox searchRow = new HBox(10);
        TextField tfSearch = searchField("Search name or department...");
        tfSearch.setPrefWidth(280);
        Button btnSearch  = accentBtn("Search");
        Button btnRefresh = ghostBtn("↺ Show All");
        searchRow.getChildren().addAll(tfSearch, btnSearch, btnRefresh);

        TableView<Employee> table = buildTable();
        TableColumn<Employee, Integer> cId    = new TableColumn<>("ID");    cId.setPrefWidth(60);
        TableColumn<Employee, String>  cName  = new TableColumn<>("Name");  cName.setPrefWidth(200);
        TableColumn<Employee, String>  cDept  = new TableColumn<>("Department"); cDept.setPrefWidth(200);
        TableColumn<Employee, String>  cEmail = new TableColumn<>("Email"); cEmail.setPrefWidth(240);
        cId.setCellValueFactory(d    -> new SimpleIntegerProperty(d.getValue().getEmployeeId()).asObject());
        cName.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue().getEmployeeName()));
        cDept.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue().getDepartment()));
        cEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        table.getColumns().addAll(cId, cName, cDept, cEmail);
        table.getItems().setAll(empSvc.getAllEmployees());

        HBox actionBar = new HBox(10);
        Button btnEdit   = warningBtn("✏ Edit Selected");
        Button btnDelete = dangerBtn("🗑 Delete Selected");
        actionBar.getChildren().addAll(btnEdit, btnDelete);
        tableCard.getChildren().addAll(searchRow, table, actionBar);

        Runnable refreshTable = () -> table.getItems().setAll(empSvc.getAllEmployees());

        btnSave.setOnAction(e -> {
            String name = tfName.getText().trim(), dept = tfDept.getText().trim(),
                   email = tfEmail.getText().trim();
            if (name.isEmpty() || dept.isEmpty()) {
                setMsg(formMsg, "⚠ Name and Department required.", C_ORANGE); return;
            }
            if (!email.isEmpty() && !email.contains("@")) {
                setMsg(formMsg, "⚠ Enter a valid email address.", C_ORANGE); return;
            }
            if (editId[0] == -1) {
                boolean ok = empSvc.addEmployee(new Employee(0, name, dept, email));
                if (ok) { setMsg(formMsg, "✅ Employee added!", C_GREEN); clearFields(tfName, tfDept, tfEmail); }
            } else {
                empSvc.updateEmployee(editId[0], name, dept, email);
                setMsg(formMsg, "✅ Employee updated!", C_GREEN);
                editId[0] = -1;
                editBadge.setVisible(false); editBadge.setManaged(false);
                btnCancel.setVisible(false); btnCancel.setManaged(false);
                btnSave.setText("＋ Save Employee");
                clearFields(tfName, tfDept, tfEmail);
            }
            refreshTable.run();
        });

        btnEdit.setOnAction(e -> {
            Employee sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Please select an employee to edit."); return; }
            tfName.setText(sel.getEmployeeName());
            tfDept.setText(sel.getDepartment());
            tfEmail.setText(sel.getEmail());
            editId[0] = sel.getEmployeeId();
            editBadge.setText("✏ Edit Mode — Employee ID: " + sel.getEmployeeId());
            editBadge.setVisible(true); editBadge.setManaged(true);
            btnCancel.setVisible(true); btnCancel.setManaged(true);
            btnSave.setText("💾 Update Employee");
            setMsg(formMsg, "", C_TEXT);
        });

        btnCancel.setOnAction(e -> {
            editId[0] = -1;
            clearFields(tfName, tfDept, tfEmail);
            editBadge.setVisible(false); editBadge.setManaged(false);
            btnCancel.setVisible(false); btnCancel.setManaged(false);
            btnSave.setText("＋ Save Employee");
            setMsg(formMsg, "", C_TEXT);
        });

        btnDelete.setOnAction(e -> {
            Employee sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Select an employee to delete."); return; }
            empSvc.deleteEmployee(sel.getEmployeeId());
            refreshTable.run();
        });

        btnSearch.setOnAction(e -> {
            String kw = tfSearch.getText().trim();
            if (kw.isEmpty()) refreshTable.run();
            else table.getItems().setAll(empSvc.searchEmployees(kw));
        });
        btnRefresh.setOnAction(e -> { tfSearch.clear(); refreshTable.run(); });

        page.getChildren().addAll(formCard, tableCard);
        scroll.setContent(page);
        setContent(scroll);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ASSIGNMENTS
    // ══════════════════════════════════════════════════════════════════════════
    private void showAssignments() {
        ScrollPane scroll = pageScroll();
        VBox page = pagePad();
        page.getChildren().add(pageHeader("Assignment Management", "Track asset check-out and check-in"));

        // Form
        VBox formCard = sectionCard("Assign Asset to Employee");
        GridPane form = buildGrid();
        TextField tfAsset = field("Asset ID", "Enter numeric Asset ID");
        TextField tfEmp   = field("Employee ID", "Enter numeric Employee ID");
        form.add(flabel("Asset ID"),    0, 0); form.add(tfAsset, 1, 0);
        form.add(flabel("Employee ID"), 0, 1); form.add(tfEmp,   1, 1);

        Button btnAssign = accentBtn("🔗 Assign Asset");
        Label assignMsg  = new Label("");
        assignMsg.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        form.add(new HBox(10, btnAssign, assignMsg), 1, 2);
        formCard.getChildren().add(form);

        // Table
        VBox tableCard = sectionCard("Assignment Records");
        HBox filterRow = new HBox(10);
        ComboBox<String> cbFilter = styledCombo("All Assignments", "Active Only", "Returned Only");
        Button btnFilter  = accentBtn("Filter");
        Button btnRefresh = ghostBtn("↺ Reset");
        filterRow.getChildren().addAll(cbFilter, btnFilter, btnRefresh);

        TableView<Assignment> table = buildTable();
        addAssignCols(table);
        table.getItems().setAll(assignSvc.getAllAssignments());

        HBox actionBar = new HBox(10);
        Button btnReturn = dangerBtn("↩ Return Asset");
        actionBar.getChildren().add(btnReturn);
        tableCard.getChildren().addAll(filterRow, table, actionBar);

        Runnable refresh = () -> table.getItems().setAll(assignSvc.getAllAssignments());

        btnAssign.setOnAction(e -> {
            try {
                int aid = Integer.parseInt(tfAsset.getText().trim());
                int eid = Integer.parseInt(tfEmp.getText().trim());
                if (aid <= 0 || eid <= 0) { setMsg(assignMsg, "⚠ IDs must be positive.", C_ORANGE); return; }
                boolean ok = assignSvc.assignAsset(new Assignment(0, aid, eid, LocalDate.now().toString()));
                if (ok) { setMsg(assignMsg, "✅ Assigned!", C_GREEN); tfAsset.clear(); tfEmp.clear(); refresh.run(); }
                else    { setMsg(assignMsg, "❌ Asset unavailable or invalid IDs.", C_ACCENT2); }
            } catch (NumberFormatException ex) {
                setMsg(assignMsg, "⚠ Enter valid numeric IDs.", C_ORANGE);
            }
        });

        btnFilter.setOnAction(e -> {
            switch (cbFilter.getValue()) {
                case "Active Only"   -> table.getItems().setAll(assignSvc.getActiveAssignments());
                case "Returned Only" -> table.getItems().setAll(
                    assignSvc.getAllAssignments().stream()
                        .filter(a -> a.getStatus().equals("Returned")).toList());
                default -> refresh.run();
            }
        });
        btnRefresh.setOnAction(e -> { cbFilter.setValue("All Assignments"); refresh.run(); });

        btnReturn.setOnAction(e -> {
            Assignment sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Select an assignment to return."); return; }
            if (sel.getStatus().equals("Returned")) { showAlert("Already returned."); return; }
            assignSvc.returnAsset(sel.getAssignmentId(), sel.getAssetId());
            refresh.run();
        });

        page.getChildren().addAll(formCard, tableCard);
        scroll.setContent(page);
        setContent(scroll);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MAINTENANCE TRACKER
    // ══════════════════════════════════════════════════════════════════════════
    private void showMaintenance() {
        ScrollPane scroll = pageScroll();
        VBox page = pagePad();
        page.getChildren().add(pageHeader("Maintenance Tracker", "Track and update asset service status"));

        List<Asset> allAssets = assetSvc.getAllAssets();
        List<Asset> inMaint   = allAssets.stream().filter(a -> a.getStatus().equals("Maintenance")).toList();
        List<Asset> retired   = allAssets.stream().filter(a -> a.getStatus().equals("Retired")).toList();

        // Summary strip
        HBox strip = new HBox(16);
        strip.getChildren().addAll(
            kpiCard("In Maintenance", String.valueOf(inMaint.size()), "🔧", C_ORANGE, "Need attention"),
            kpiCard("Retired Assets", String.valueOf(retired.size()),  "📦", C_MUTED,  "Decommissioned"),
            kpiCard("Available",
                String.valueOf(allAssets.stream().filter(a -> a.getStatus().equals("Available")).count()),
                "✅", C_GREEN, "Ready to use")
        );

        // Assets in maintenance
        VBox maintCard = sectionCard("Assets in Maintenance");
        TableView<Asset> maintTbl = buildTable();
        TableColumn<Asset, Integer> mId     = new TableColumn<>("ID");     mId.setPrefWidth(60);
        TableColumn<Asset, String>  mName   = new TableColumn<>("Asset Name"); mName.setPrefWidth(200);
        TableColumn<Asset, String>  mSerial = new TableColumn<>("Serial No."); mSerial.setPrefWidth(160);
        TableColumn<Asset, String>  mCat    = new TableColumn<>("Category"); mCat.setPrefWidth(130);
        TableColumn<Asset, String>  mStatus = new TableColumn<>("Status"); mStatus.setPrefWidth(130);
        mId.setCellValueFactory(d     -> new SimpleIntegerProperty(d.getValue().getAssetId()).asObject());
        mName.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getAssetName()));
        mSerial.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSerialNumber()));
        mCat.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue().getCategory()));
        mStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        statusColorCell(mStatus);
        maintTbl.getColumns().addAll(mId, mName, mSerial, mCat, mStatus);
        maintTbl.getItems().setAll(inMaint);

        // Quick status changer
        HBox changeBar = new HBox(12);
        changeBar.setAlignment(Pos.CENTER_LEFT);
        Label changeLabel = new Label("Change status of selected asset:");
        changeLabel.setFont(Font.font("Verdana", 12));
        changeLabel.setTextFill(Color.web(C_MUTED));
        ComboBox<String> cbNewStatus = styledCombo("Available", "Maintenance", "Retired");
        Button btnChange = accentBtn("Apply Status");
        Label changeMsg  = new Label("");
        changeMsg.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        changeBar.getChildren().addAll(changeLabel, cbNewStatus, btnChange, changeMsg);

        btnChange.setOnAction(e -> {
            Asset sel = maintTbl.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Select an asset to update."); return; }
            assetSvc.updateAssetStatus(sel.getAssetId(), cbNewStatus.getValue());
            setMsg(changeMsg, "✅ Status updated to " + cbNewStatus.getValue(), C_GREEN);
            List<Asset> fresh = assetSvc.getAllAssets()
                .stream().filter(a -> a.getStatus().equals("Maintenance")).toList();
            maintTbl.getItems().setAll(fresh);
        });

        maintCard.getChildren().addAll(maintTbl, changeBar);

        // All assets quick-status table
        VBox allCard = sectionCard("All Assets — Quick Status Manager");
        TableView<Asset> allTbl = buildTable();
        TableColumn<Asset, Integer> aId     = new TableColumn<>("ID");     aId.setPrefWidth(60);
        TableColumn<Asset, String>  aName   = new TableColumn<>("Name");   aName.setPrefWidth(200);
        TableColumn<Asset, String>  aSerial = new TableColumn<>("Serial"); aSerial.setPrefWidth(150);
        TableColumn<Asset, String>  aCat    = new TableColumn<>("Category"); aCat.setPrefWidth(130);
        TableColumn<Asset, Double>  aPrice  = new TableColumn<>("Price (₹)"); aPrice.setPrefWidth(110);
        TableColumn<Asset, String>  aStatus = new TableColumn<>("Status"); aStatus.setPrefWidth(130);
        aId.setCellValueFactory(d     -> new SimpleIntegerProperty(d.getValue().getAssetId()).asObject());
        aName.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getAssetName()));
        aSerial.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSerialNumber()));
        aCat.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue().getCategory()));
        aPrice.setCellValueFactory(d  -> new SimpleDoubleProperty(d.getValue().getPurchasePrice()).asObject());
        aStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        statusColorCell(aStatus);
        allTbl.getColumns().addAll(aId, aName, aSerial, aCat, aPrice, aStatus);
        allTbl.getItems().setAll(allAssets);

        HBox allBar = new HBox(12);
        allBar.setAlignment(Pos.CENTER_LEFT);
        Label allLbl = new Label("Change selected:");
        allLbl.setFont(Font.font("Verdana", 12));
        allLbl.setTextFill(Color.web(C_MUTED));
        ComboBox<String> cbAll = styledCombo("Available", "Assigned", "Maintenance", "Retired");
        Button btnAll  = accentBtn("Apply");
        Label  allMsg  = new Label("");
        allMsg.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        allBar.getChildren().addAll(allLbl, cbAll, btnAll, allMsg);

        btnAll.setOnAction(e -> {
            Asset sel = allTbl.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Select an asset."); return; }
            assetSvc.updateAssetStatus(sel.getAssetId(), cbAll.getValue());
            setMsg(allMsg, "✅ Updated.", C_GREEN);
            allTbl.getItems().setAll(assetSvc.getAllAssets());
        });

        allCard.getChildren().addAll(allTbl, allBar);

        page.getChildren().addAll(strip, maintCard, allCard);
        scroll.setContent(page);
        setContent(scroll);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SQL SUMMARY REPORT
    // ══════════════════════════════════════════════════════════════════════════
    private void showSQLReport() {
        ScrollPane scroll = pageScroll();
        VBox page = pagePad();
        page.getChildren().add(pageHeader("SQL Summary Report", "Live database-driven analytics"));

        List<Asset>      assets  = assetSvc.getAllAssets();
        List<Employee>   emps    = empSvc.getAllEmployees();
        List<Assignment> assigns = assignSvc.getAllAssignments();
        List<Assignment> active  = assignSvc.getActiveAssignments();

        // ── Aggregate stats ──────────────────────────────────────────────────
        long avail   = assets.stream().filter(a -> a.getStatus().equals("Available")).count();
        long asgn    = assets.stream().filter(a -> a.getStatus().equals("Assigned")).count();
        long maint   = assets.stream().filter(a -> a.getStatus().equals("Maintenance")).count();
        long retired = assets.stream().filter(a -> a.getStatus().equals("Retired")).count();
        double totalValue    = assets.stream().mapToDouble(Asset::getPurchasePrice).sum();
        double avgValue      = assets.isEmpty() ? 0 : totalValue / assets.size();
        double assignedValue = assets.stream()
            .filter(a -> a.getStatus().equals("Assigned"))
            .mapToDouble(Asset::getPurchasePrice).sum();

        // ── Stats grid ───────────────────────────────────────────────────────
        VBox statsCard = sectionCard("📊 Aggregate Statistics");
        GridPane sg = new GridPane();
        sg.setHgap(30); sg.setVgap(12);
        sg.setPadding(new Insets(10, 0, 0, 0));

        addStatRow(sg, 0, "Total Assets",        String.valueOf(assets.size()));
        addStatRow(sg, 1, "Total Employees",      String.valueOf(emps.size()));
        addStatRow(sg, 2, "Total Assignments",    String.valueOf(assigns.size()));
        addStatRow(sg, 3, "Active Assignments",   String.valueOf(active.size()));
        addStatRow(sg, 4, "Available Assets",     String.valueOf(avail));
        addStatRow(sg, 5, "Assigned Assets",      String.valueOf(asgn));
        addStatRow(sg, 6, "In Maintenance",       String.valueOf(maint));
        addStatRow(sg, 7, "Retired Assets",       String.valueOf(retired));
        addStatRow(sg, 8, "Total Asset Value",    String.format("₹ %,.2f", totalValue));
        addStatRow(sg, 9, "Average Asset Value",  String.format("₹ %,.2f", avgValue));
        addStatRow(sg,10, "Value Currently Deployed", String.format("₹ %,.2f", assignedValue));
        statsCard.getChildren().add(sg);

        // ── Assets per category ──────────────────────────────────────────────
        VBox catCard = sectionCard("📦 Assets by Category");
        TableView<String[]> catTbl = rawTable();
        TableColumn<String[], String> cc1 = rawCol("Category", 0, 220);
        TableColumn<String[], String> cc2 = rawCol("Count",    1, 100);
        TableColumn<String[], String> cc3 = rawCol("Total Value (₹)", 2, 200);
        catTbl.getColumns().addAll(cc1, cc2, cc3);
        Map<String, List<Asset>> bycat = assets.stream()
            .collect(Collectors.groupingBy(Asset::getCategory));
        for (Map.Entry<String, List<Asset>> en : bycat.entrySet()) {
            double val = en.getValue().stream().mapToDouble(Asset::getPurchasePrice).sum();
            catTbl.getItems().add(new String[]{
                en.getKey(),
                String.valueOf(en.getValue().size()),
                String.format("₹ %,.2f", val)
            });
        }
        catCard.getChildren().add(catTbl);

        // ── Department assignment summary ────────────────────────────────────
        VBox deptCard = sectionCard("👥 Assignments by Department");
        TableView<String[]> deptTbl = rawTable();
        TableColumn<String[], String> dc1 = rawCol("Department", 0, 220);
        TableColumn<String[], String> dc2 = rawCol("Employees",  1, 120);
        deptTbl.getColumns().addAll(dc1, dc2);
        emps.stream()
            .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()))
            .forEach((dept, cnt) -> deptTbl.getItems().add(new String[]{dept, String.valueOf(cnt)}));
        deptCard.getChildren().add(deptTbl);

        // ── Top assigned employees ───────────────────────────────────────────
        VBox topCard = sectionCard("🏆 Most Assigned Employees");
        TableView<String[]> topTbl = rawTable();
        TableColumn<String[], String> tc1 = rawCol("Employee Name", 0, 220);
        TableColumn<String[], String> tc2 = rawCol("Department",    1, 180);
        TableColumn<String[], String> tc3 = rawCol("Assignments",   2, 120);
        topTbl.getColumns().addAll(tc1, tc2, tc3);
        Map<Integer, Long> empCounts = assigns.stream()
            .collect(Collectors.groupingBy(Assignment::getEmployeeId, Collectors.counting()));
        empCounts.entrySet().stream()
            .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
            .limit(10)
            .forEach(en -> {
                Employee emp = emps.stream()
                    .filter(e2 -> e2.getEmployeeId() == en.getKey())
                    .findFirst().orElse(null);
                if (emp != null)
                    topTbl.getItems().add(new String[]{
                        emp.getEmployeeName(), emp.getDepartment(), String.valueOf(en.getValue())
                    });
            });
        topCard.getChildren().add(topTbl);

        page.getChildren().addAll(statsCard, catCard, deptCard, topCard);
        scroll.setContent(page);
        setContent(scroll);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DEPRECIATION CALCULATOR
    // ══════════════════════════════════════════════════════════════════════════
    private void showDepreciation() {
        ScrollPane scroll = pageScroll();
        VBox page = pagePad();
        page.getChildren().add(pageHeader("Depreciation Calculator",
                "Straight-line depreciation based on purchase price"));

        VBox infoCard = sectionCard("📘 How It Works");
        Label info = new Label(
            "Formula:  Annual Depreciation  =  (Purchase Price - Salvage Value) ÷ Useful Life\n" +
            "Salvage Value is assumed to be 10% of Purchase Price.\n" +
            "Useful Life defaults: Electronics = 3 yrs  |  Vehicles = 5 yrs  |  Others = 7 yrs");
        info.setFont(Font.font("Verdana", 12));
        info.setTextFill(Color.web(C_MUTED));
        info.setWrapText(true);
        infoCard.getChildren().add(info);

        VBox calcCard = sectionCard("🧮 Manual Calculator");
        GridPane cForm = buildGrid();
        TextField tfCPrice   = field("Purchase Price",     "e.g. 55000");
        TextField tfCSalvage = field("Salvage Value",      "Leave blank for auto 10%");
        TextField tfCLife    = field("Useful Life (yrs)",  "e.g. 3");
        cForm.add(flabel("Purchase Price"),    0, 0); cForm.add(tfCPrice,   1, 0);
        cForm.add(flabel("Salvage Value"),     0, 1); cForm.add(tfCSalvage, 1, 1);
        cForm.add(flabel("Useful Life (yrs)"), 0, 2); cForm.add(tfCLife,    1, 2);
        Button btnCalc = accentBtn("Calculate");
        cForm.add(btnCalc, 1, 3);

        VBox resultBox = new VBox(10);
        resultBox.setPadding(new Insets(16));
        resultBox.setStyle("-fx-background-color: " + C_SURFACE + "; -fx-background-radius: 10;");
        resultBox.setVisible(false); resultBox.setManaged(false);
        Label rAnnual  = new Label(""); rAnnual.setFont(Font.font("Verdana", FontWeight.BOLD, 13)); rAnnual.setTextFill(Color.web(C_GREEN));
        Label rMonthly = new Label(""); rMonthly.setFont(Font.font("Verdana", 12)); rMonthly.setTextFill(Color.web(C_TEXT));
        Label rTotal   = new Label(""); rTotal.setFont(Font.font("Verdana", 12)); rTotal.setTextFill(Color.web(C_TEXT));
        Label rSalvage = new Label(""); rSalvage.setFont(Font.font("Verdana", 12)); rSalvage.setTextFill(Color.web(C_MUTED));
        resultBox.getChildren().addAll(rAnnual, rMonthly, rTotal, rSalvage);
        cForm.add(resultBox, 1, 4);
        calcCard.getChildren().add(cForm);

        btnCalc.setOnAction(e -> {
            try {
                double price   = Double.parseDouble(tfCPrice.getText().trim());
                double salvage = tfCSalvage.getText().trim().isEmpty()
                    ? price * 0.10 : Double.parseDouble(tfCSalvage.getText().trim());
                int    life    = Integer.parseInt(tfCLife.getText().trim());
                if (price <= 0 || life <= 0) { showAlert("Price and Life must be positive."); return; }
                double annual  = (price - salvage) / life;
                rAnnual.setText( "Annual Depreciation   :  ₹ " + String.format("%,.2f", annual));
                rMonthly.setText("Monthly Depreciation  :  ₹ " + String.format("%,.2f", annual / 12));
                rTotal.setText(  "Total Depreciation    :  ₹ " + String.format("%,.2f", price - salvage));
                rSalvage.setText("Final Salvage Value   :  ₹ " + String.format("%,.2f", salvage));
                resultBox.setVisible(true); resultBox.setManaged(true);
            } catch (NumberFormatException ex) { showAlert("Please enter valid numbers."); }
        });

        VBox autoCard = sectionCard("📦 All Assets — Auto Depreciation Table");
        Label autoInfo = new Label("Electronics/Network = 3 yrs  |  Vehicles = 5 yrs  |  Others = 7 yrs  |  Salvage = 10%");
        autoInfo.setFont(Font.font("Verdana", 10)); autoInfo.setTextFill(Color.web(C_MUTED));

        TableView<String[]> dTable = rawTable(); dTable.setPrefHeight(350);
        dTable.getColumns().addAll(
            rawCol("ID", 0, 55), rawCol("Asset Name", 1, 175), rawCol("Category", 2, 120),
            rawCol("Purchase Price", 3, 130), rawCol("Useful Life", 4, 90),
            rawCol("Annual Depr.", 5, 125), rawCol("Monthly Depr.", 6, 125),
            rawCol("Salvage Value", 7, 125));

        for (Asset a : assetSvc.getAllAssets()) {
            String catLow = a.getCategory().toLowerCase();
            int life = catLow.equals("electronics") || catLow.equals("network") ? 3
                     : catLow.equals("vehicles") ? 5 : 7;
            double salvage = a.getPurchasePrice() * 0.10;
            double annual  = (a.getPurchasePrice() - salvage) / life;
            dTable.getItems().add(new String[]{
                String.valueOf(a.getAssetId()), a.getAssetName(), a.getCategory(),
                String.format("₹ %,.2f", a.getPurchasePrice()), life + " yrs",
                String.format("₹ %,.2f", annual), String.format("₹ %,.2f", annual / 12),
                String.format("₹ %,.2f", salvage)
            });
        }
        autoCard.getChildren().addAll(autoInfo, dTable);
        page.getChildren().addAll(infoCard, calcCard, autoCard);
        scroll.setContent(page); setContent(scroll);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DEPARTMENT-WISE ASSET VIEW
    // ══════════════════════════════════════════════════════════════════════════
    private void showDepartments() {
        ScrollPane scroll = pageScroll();
        VBox page = pagePad();
        page.getChildren().add(pageHeader("Department View",
                "Assets and employees grouped by department"));

        List<Employee>   emps    = empSvc.getAllEmployees();
        List<Asset>      assets  = assetSvc.getAllAssets();
        List<Assignment> assigns = assignSvc.getAllAssignments();
        List<String> departments = emps.stream()
            .map(Employee::getDepartment).distinct().sorted().toList();

        if (departments.isEmpty()) {
            page.getChildren().add(sectionCard("No departments found — add employees first."));
            scroll.setContent(page); setContent(scroll); return;
        }

        // Summary table
        VBox summaryCard = sectionCard("📊 Department Summary");
        TableView<String[]> sumTbl = rawTable(); sumTbl.setPrefHeight(200);
        sumTbl.getColumns().addAll(
            rawCol("Department", 0, 200), rawCol("Employees", 1, 110),
            rawCol("Assets Assigned", 2, 140), rawCol("Total Asset Value", 3, 180));

        for (String dept : departments) {
            List<Employee> dEmps = emps.stream().filter(e -> e.getDepartment().equals(dept)).toList();
            List<Integer>  eIds  = dEmps.stream().map(Employee::getEmployeeId).toList();
            List<Assignment> dA  = assigns.stream()
                .filter(a -> eIds.contains(a.getEmployeeId()) && a.getStatus().equals("Assigned")).toList();
            double val = dA.stream().mapToDouble(a -> assets.stream()
                .filter(x -> x.getAssetId() == a.getAssetId())
                .mapToDouble(Asset::getPurchasePrice).findFirst().orElse(0)).sum();
            sumTbl.getItems().add(new String[]{dept, String.valueOf(dEmps.size()),
                String.valueOf(dA.size()), String.format("₹ %,.2f", val)});
        }
        summaryCard.getChildren().add(sumTbl);

        // Filter + Detail table
        VBox filterCard = sectionCard("Filter by Department");
        HBox filterRow  = new HBox(12); filterRow.setAlignment(Pos.CENTER_LEFT);
        ComboBox<String> cbDept = new ComboBox<>();
        cbDept.getItems().add("All Departments"); cbDept.getItems().addAll(departments);
        cbDept.setValue("All Departments");
        cbDept.setStyle("-fx-background-color:" + C_SURFACE + ";-fx-text-fill:" + C_TEXT +
            ";-fx-border-color:" + C_BORDER + ";-fx-border-radius:7;-fx-background-radius:7;");
        cbDept.setPrefWidth(220);
        Button btnGo = accentBtn("View");
        filterRow.getChildren().addAll(flabel("Department:"), cbDept, btnGo);
        filterCard.getChildren().add(filterRow);

        VBox detailCard = sectionCard("👥 Employee — Asset Details");
        TableView<String[]> detTbl = rawTable(); detTbl.setPrefHeight(350);
        detTbl.getColumns().addAll(
            rawCol("Emp ID", 0, 70), rawCol("Employee", 1, 175),
            rawCol("Department", 2, 155), rawCol("Asset ID", 3, 80),
            rawCol("Asset Name", 4, 175), rawCol("Status", 5, 110),
            rawCol("Since", 6, 115));

        Runnable populate = () -> {
            detTbl.getItems().clear();
            String sel = cbDept.getValue();
            List<Employee> filtered = "All Departments".equals(sel) ? emps
                : emps.stream().filter(e -> e.getDepartment().equals(sel)).toList();
            for (Employee emp : filtered) {
                List<Assignment> ea = assigns.stream()
                    .filter(a -> a.getEmployeeId() == emp.getEmployeeId()
                              && a.getStatus().equals("Assigned")).toList();
                if (ea.isEmpty()) {
                    detTbl.getItems().add(new String[]{
                        String.valueOf(emp.getEmployeeId()), emp.getEmployeeName(),
                        emp.getDepartment(), "—", "No asset assigned", "—", "—"});
                } else {
                    for (Assignment a : ea) {
                        Asset ast = assets.stream()
                            .filter(x -> x.getAssetId() == a.getAssetId()).findFirst().orElse(null);
                        detTbl.getItems().add(new String[]{
                            String.valueOf(emp.getEmployeeId()), emp.getEmployeeName(),
                            emp.getDepartment(), String.valueOf(a.getAssetId()),
                            ast != null ? ast.getAssetName() : "Unknown",
                            ast != null ? ast.getStatus()    : "Unknown",
                            a.getAssignedDate()});
                    }
                }
            }
        };
        populate.run();
        btnGo.setOnAction(e -> populate.run());
        detailCard.getChildren().add(detTbl);

        page.getChildren().addAll(summaryCard, filterCard, detailCard);
        scroll.setContent(page); setContent(scroll);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PRINT REPORT
    // ══════════════════════════════════════════════════════════════════════════
    // ══════════════════════════════════════════════════════════════════════════
    //  PDF REPORT  —  Real vector text PDF, no image blur
    // ══════════════════════════════════════════════════════════════════════════
    private void showPdfReport() {
        try {
            List<Asset>      assets  = assetSvc.getAllAssets();
            List<Employee>   emps    = empSvc.getAllEmployees();
            List<Assignment> assigns = assignSvc.getAllAssignments();
            List<Assignment> active  = assignSvc.getActiveAssignments();
            long avail   = assets.stream().filter(a->a.getStatus().equals("Available")).count();
            long asgn    = assets.stream().filter(a->a.getStatus().equals("Assigned")).count();
            long maint   = assets.stream().filter(a->a.getStatus().equals("Maintenance")).count();
            long ret     = assets.stream().filter(a->a.getStatus().equals("Retired")).count();
            double totalVal = assets.stream().mapToDouble(Asset::getPurchasePrice).sum();
            double avgVal   = assets.isEmpty() ? 0 : totalVal/assets.size();
            double utilRate = assets.isEmpty() ? 0 : (double)asgn/assets.size()*100;
            String ts    = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm"));
            String fname = "FAST_Report_" + java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

            PdfBuilder pdf = new PdfBuilder();
            pdf.build(assets, emps, assigns, active,
                      avail, asgn, maint, ret, totalVal, avgVal, utilRate, ts, fname);

            java.io.File pdfFile = new java.io.File(fname);
            if (java.awt.Desktop.isDesktopSupported())
                java.awt.Desktop.getDesktop().open(pdfFile);

            Label ok = new Label("PDF Generated & Opened!");
            ok.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
            ok.setTextFill(Color.web(C_GREEN));
            Label pathLbl = new Label(pdfFile.getAbsolutePath());
            pathLbl.setFont(Font.font("Verdana", 10));
            pathLbl.setTextFill(Color.web(C_MUTED));
            pathLbl.setWrapText(true);
            Button btnOpen = new Button("Open Again");
            btnOpen.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
            btnOpen.setStyle("-fx-background-color:"+C_ACCENT+";-fx-text-fill:white;-fx-background-radius:8;-fx-padding:9 22;");
            btnOpen.setCursor(javafx.scene.Cursor.HAND);
            btnOpen.setOnAction(ev->{ try{java.awt.Desktop.getDesktop().open(pdfFile);}catch(Exception ignored){} });
            Button btnNew = new Button("Regenerate");
            btnNew.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
            btnNew.setStyle("-fx-background-color:#1e1e3a;-fx-text-fill:"+C_MUTED+";-fx-background-radius:8;-fx-padding:9 22;-fx-border-color:"+C_BORDER+";-fx-border-radius:8;");
            btnNew.setCursor(javafx.scene.Cursor.HAND);
            btnNew.setOnAction(ev->showPdfReport());
            HBox btnRow = new HBox(12, btnOpen, btnNew);
            btnRow.setAlignment(Pos.CENTER_LEFT);
            VBox pg = new VBox(16, ok, pathLbl, btnRow);
            pg.setPadding(new Insets(32,36,40,36));
            pg.setStyle("-fx-background-color:"+C_BG+";");
            ScrollPane sc = new ScrollPane(pg); sc.setFitToWidth(true);
            sc.setStyle("-fx-background:"+C_BG+";-fx-background-color:"+C_BG+";");
            setContent(sc);

        } catch (Exception ex) {
            java.io.StringWriter sw = new java.io.StringWriter();
            ex.printStackTrace(new java.io.PrintWriter(sw));
            Label err = new Label("PDF Error: " + ex.getMessage());
            err.setFont(Font.font("Verdana",12)); err.setTextFill(Color.web(C_ACCENT2)); err.setWrapText(true);
            TextArea tr = new TextArea(sw.toString());
            tr.setEditable(false); tr.setPrefHeight(300); tr.setFont(Font.font("Courier New",9));
            Button retry = new Button("Retry");
            retry.setStyle("-fx-background-color:"+C_ACCENT+";-fx-text-fill:white;-fx-background-radius:8;-fx-padding:8 20;");
            retry.setOnAction(e->showPdfReport());
            VBox ep = new VBox(12,err,tr,retry); ep.setPadding(new Insets(28)); ep.setStyle("-fx-background-color:"+C_BG+";");
            ScrollPane sp = new ScrollPane(ep); sp.setFitToWidth(true); sp.setStyle("-fx-background:"+C_BG+";");
            setContent(sp);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PdfBuilder — pure Java, real vector text, A4 PDF
    // ══════════════════════════════════════════════════════════════════════════
    private static class PdfBuilder {
        // A4: 595 x 842 pts  (1 pt = 1/72 inch)
        static final float PW = 595, PH = 842, M = 36;
        static final float CW = PW - M*2;

        // RGB helpers  (PDF uses 0-1 floats)
        static String rgb(int r,int g,int b){ return String.format("%.3f %.3f %.3f",r/255f,g/255f,b/255f); }
        // Named colors
        static final String BG    = rgb(10,10,22);
        static final String CARD  = rgb(18,18,40);
        static final String ROWA  = rgb(22,22,50);
        static final String ROWB  = rgb(15,15,35);
        static final String HDRB  = rgb(28,28,62);
        static final String SECBG = rgb(30,26,70);
        static final String BRDR  = rgb(50,50,100);
        static final String ACC   = rgb(124,106,247);
        static final String GRN   = rgb(46,204,113);
        static final String ORG   = rgb(243,156,18);
        static final String RED   = rgb(231,76,60);
        static final String BLU   = rgb(59,130,246);
        static final String PUR   = rgb(155,89,182);
        static final String TXT   = rgb(230,230,255);
        static final String MUT   = rgb(130,130,180);
        static final String WHT   = rgb(255,255,255);
        static final String[][] CAT_COLS = {{ACC},{GRN},{BLU},{ORG},{PUR},{RED}};
        static final String[] S_COLS = {GRN, BLU, ORG, RED};
        static final String[] S_NAMES = {"Available","Assigned","Maintenance","Retired"};

        // PDF object storage
        java.util.List<String> objects = new java.util.ArrayList<>();
        java.util.List<Integer> offsets = new java.util.ArrayList<>();
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        int pos = 0;

        void write(String s) throws Exception {
            byte[] b = s.getBytes("ISO-8859-1"); buf.write(b); pos += b.length;
        }

        int addObj(String content) {
            objects.add(content);
            return objects.size(); // 1-indexed
        }

        // ── Stream content builder for a PDF page ─────────────────────────────
        static class PageStream {
            StringBuilder sb = new StringBuilder();

            // Fill rectangle (y from bottom in PDF coords)
            PageStream rect(float x, float y, float w, float h, String color) {
                sb.append(color).append(" rg  ").append(color).append(" RG\n");
                sb.append(String.format("%.2f %.2f %.2f %.2f re f\n", x, y, w, h));
                return this;
            }
            // Rounded rect via Bezier curves
            PageStream rrect(float x, float y, float w, float h, float r, String fill) {
                sb.append(fill).append(" rg\n");
                float k = 0.5523f * r;
                sb.append(String.format("%.2f %.2f m\n", x+r, y));
                sb.append(String.format("%.2f %.2f %.2f %.2f %.2f %.2f c\n", x+r-k,y, x,y+k, x,y+r));
                sb.append(String.format("%.2f %.2f l\n", x, y+h-r));
                sb.append(String.format("%.2f %.2f %.2f %.2f %.2f %.2f c\n", x,y+h-r+k, x+r-k,y+h, x+r,y+h));
                sb.append(String.format("%.2f %.2f l\n", x+w-r, y+h));
                sb.append(String.format("%.2f %.2f %.2f %.2f %.2f %.2f c\n", x+w-r+k,y+h, x+w,y+h-r+k, x+w,y+h-r));
                sb.append(String.format("%.2f %.2f l\n", x+w, y+r));
                sb.append(String.format("%.2f %.2f %.2f %.2f %.2f %.2f c\n", x+w,y+r-k, x+w-r+k,y, x+w-r,y));
                sb.append("h f\n");
                return this;
            }
            // Circle
            PageStream circle(float cx, float cy, float r, String fill) {
                sb.append(fill).append(" rg\n");
                float k = 0.5523f * r;
                sb.append(String.format("%.2f %.2f m\n", cx, cy+r));
                sb.append(String.format("%.2f %.2f %.2f %.2f %.2f %.2f c\n", cx+k,cy+r, cx+r,cy+k, cx+r,cy));
                sb.append(String.format("%.2f %.2f %.2f %.2f %.2f %.2f c\n", cx+r,cy-k, cx+k,cy-r, cx,cy-r));
                sb.append(String.format("%.2f %.2f %.2f %.2f %.2f %.2f c\n", cx-k,cy-r, cx-r,cy-k, cx-r,cy));
                sb.append(String.format("%.2f %.2f %.2f %.2f %.2f %.2f c\n", cx-r,cy+k, cx-k,cy+r, cx,cy+r));
                sb.append("h f\n");
                return this;
            }
            // Line
            PageStream line(float x1, float y1, float x2, float y2, String color, float lw) {
                sb.append(color).append(" RG  ").append(String.format("%.1f w\n", lw));
                sb.append(String.format("%.2f %.2f m %.2f %.2f l S\n", x1,y1,x2,y2));
                return this;
            }
            // Text (x,y = baseline, PDF coords bottom-left origin)
            PageStream text(float x, float y, String txt, float size, boolean bold, String color) {
                if (txt == null || txt.isEmpty()) return this;
                // Escape special PDF string chars
                txt = txt.replace("\\","\\\\").replace("(","\\(").replace(")","\\)");
                String font = bold ? "/FB" : "/FR";
                sb.append(String.format("BT %s %.1f Tf %s rg %.2f %.2f Td (%s) Tj ET\n",
                    font, size, color, x, y, txt));
                return this;
            }
            // Right-aligned text
            PageStream textR(float rightX, float y, String txt, float size, boolean bold, String color, java.awt.Font f) {
                if (txt==null||txt.isEmpty()) return this;
                java.awt.FontMetrics fm = new java.awt.Canvas().getFontMetrics(
                    new java.awt.Font("Arial", bold ? java.awt.Font.BOLD : java.awt.Font.PLAIN, (int)(size*1.33)));
                int w = fm.stringWidth(txt);
                float scale = size / (size*1.33f);
                float x = rightX - w * scale * 0.75f;
                return text(x, y, txt, size, bold, color);
            }
            String get() { return sb.toString(); }
        }

        // ── PDF coordinate: PDF origin is bottom-left, so y = PH - top ────────
        static float py(float topY) { return PH - topY; }

        void build(List<Asset> assets, List<Employee> emps, List<Assignment> assigns,
                   List<Assignment> active, long avail, long asgn, long maint, long ret,
                   double totalVal, double avgVal, double utilRate, String ts, String fname) throws Exception {

            // ═══════════════════════════════════════════════════════════════
            // PAGE 1 CONTENT STREAM
            // ═══════════════════════════════════════════════════════════════
            PageStream p1 = new PageStream();

            // Background
            p1.rect(0, 0, PW, PH, BG);
            // Subtle header gradient overlay — use slightly lighter bg at top
            p1.rect(0, PH-120, PW, 120, rgb(20,16,60));

            // Top accent bar gradient (simulated with 3 rects)
            p1.rect(0, PH-5, PW*0.33f, 5, ACC);
            p1.rect(PW*0.33f, PH-5, PW*0.34f, 5, rgb(90,118,247));
            p1.rect(PW*0.67f, PH-5, PW*0.34f, 5, BLU);
            // Left accent strip
            p1.rect(0, 0, 3, PH, ACC);

            // ── College badge ──────────────────────────────────────────────
            p1.rrect(M, PH-60, 105, 30, 5, CARD);
            p1.rect(M, PH-60, 3, 30, ACC);
            p1.text(M+8, PH-38, "MJ COLLEGE", 9, true, ACC);
            p1.text(M+8, PH-50, "Engineering & Technology", 7, false, MUT);

            // Date top right
            p1.text(PW-M-180, PH-36, "Generated: "+ts, 7, false, MUT);
            p1.text(PW-M-80,  PH-48, "OFFICIAL  REPORT", 7, true, GRN);

            // ── Hero title ─────────────────────────────────────────────────
            p1.text(M, PH-96,  "Fixed Asset Tracking", 28, true, TXT);
            p1.text(M, PH-126, "System Report",         28, true, ACC);
            // Gradient underline (2 rects)
            p1.rect(M,       PH-130, CW*0.5f, 3, ACC);
            p1.rect(M+CW*0.5f, PH-130, CW*0.5f, 3, BLU);
            p1.text(M, PH-142, "Muffakham Jah College of Engineering & Technology  |  MJ-IRP 2025-26", 8, false, MUT);

            // ── Portfolio value box ────────────────────────────────────────
            float bx=PW-M-155, by=PH-145, bw=155, bh=62;
            p1.rrect(bx, by, bw, bh, 6, CARD);
            p1.rect(bx, by+bh-3, bw, 3, GRN);  // top strip
            p1.rect(bx, by, 3, bh, GRN);        // left strip
            p1.text(bx+10, by+bh-14, "TOTAL PORTFOLIO VALUE", 7, true, MUT);
            p1.text(bx+10, by+bh-32, "Rs. "+String.format("%,.0f",totalVal), 16, true, GRN);
            p1.text(bx+10, by+14, "Avg: Rs. "+String.format("%,.0f",avgVal)+" / asset", 7, false, MUT);

            // ── KPI cards ─────────────────────────────────────────────────
            int[] kV = {(int)assets.size(),(int)avail,(int)asgn,(int)maint,(int)ret,emps.size()};
            String[] kL = {"Total Assets","Available","Assigned","Maintenance","Retired","Employees"};
            String[] kC = {ACC, GRN, BLU, ORG, RED, PUR};
            float kW=78, kH=52, kGap=4;
            float kY = PH-210;
            for(int i=0;i<6;i++){
                float kX = M + i*(kW+kGap);
                p1.rrect(kX, kY, kW, kH, 5, CARD);
                // top strip
                p1.rect(kX, kY+kH-3, kW, 3, kC[i]);
                // left micro strip
                p1.rect(kX, kY, 2, kH, kC[i]);
                // value centered
                String vs = String.valueOf(kV[i]);
                float valX = kX + kW/2 - vs.length()*7.5f; // approx center
                p1.text(valX, kY+kH-26, vs, 22, true, kC[i]);
                // label centered
                float lblX = kX + kW/2 - kL[i].length()*3f;
                p1.text(lblX, kY+8, kL[i], 6, true, MUT);
            }

            float sy = PH - 230; // top of next section (PDF coords = from bottom)

            // ── Section header helper ──────────────────────────────────────
            // Section 1
            p1.rrect(M, sy-20, CW, 20, 4, SECBG);
            p1.rect(M, sy-20, 4, 20, ACC);
            p1.text(M+10, sy-14, "SECTION 1   EXECUTIVE SUMMARY", 10, true, WHT);
            sy -= 22;

            // Column header row
            p1.rect(M, sy-14, CW, 14, HDRB);
            p1.text(M+6,      sy-10, "Description",   8, true, MUT);
            p1.text(M+CW-80,  sy-10, "Value",          8, true, MUT);
            sy -= 14;

            float RH = 16;
            String[][] rows1 = {
                {"Total Assets",       String.valueOf(assets.size())},
                {"Total Asset Value",  "Rs. "+String.format("%,.2f",totalVal)},
                {"Available",          String.valueOf(avail)},
                {"Assigned",           String.valueOf(asgn)},
                {"Under Maintenance",  String.valueOf(maint)},
                {"Retired",            String.valueOf(ret)},
                {"Total Employees",    String.valueOf(emps.size())},
                {"Total Assignments",  String.valueOf(assigns.size())},
                {"Active Deployments", String.valueOf(active.size())},
                {"Utilization Rate",   String.format("%.1f%%",utilRate)},
            };
            for(int i=0;i<rows1.length;i++){
                String bg2 = i%2==0 ? ROWA : ROWB;
                p1.rect(M, sy-RH, CW, RH, bg2);
                p1.rect(M, sy-RH, 2, RH, i%2==0 ? ACC : rgb(30,30,70));
                p1.text(M+8,        sy-RH+5, rows1[i][0], 8, false, MUT);
                String vc = rows1[i][0].contains("Rate")||rows1[i][0].contains("Value") ? GRN : TXT;
                // right-align value
                float vw = rows1[i][1].length() * 5.8f;
                p1.text(M+CW-vw-6,  sy-RH+5, rows1[i][1], 8, true,  vc);
                sy -= RH;
            }
            p1.line(M, sy, M+CW, sy, BRDR, 0.5f);
            sy -= 18;

            // Section 2
            p1.rrect(M, sy-20, CW, 20, 4, rgb(15,30,80));
            p1.rect(M, sy-20, 4, 20, BLU);
            p1.text(M+10, sy-14, "SECTION 2   ASSETS BY CATEGORY", 10, true, WHT);
            sy -= 22;

            // Col headers
            p1.rect(M, sy-13, CW, 13, HDRB);
            p1.text(M+8,       sy-9, "Category",    7, true, MUT);
            p1.text(M+160,     sy-9, "Count",        7, true, MUT);
            p1.text(M+220,     sy-9, "Total Value",  7, true, MUT);
            p1.text(M+380,     sy-9, "% Share",      7, true, MUT);
            sy -= 13;

            java.util.Map<String,double[]> catMap = new java.util.LinkedHashMap<>();
            for(Asset a:assets){
                catMap.computeIfAbsent(a.getCategory(),k->new double[]{0,0});
                catMap.get(a.getCategory())[0]++;
                catMap.get(a.getCategory())[1]+=a.getPurchasePrice();
            }
            String[] catCols = {ACC,GRN,BLU,ORG,PUR,RED};
            int ci=0;
            float catRH=18;
            for(java.util.Map.Entry<String,double[]> e:catMap.entrySet()){
                double pct=totalVal>0?e.getValue()[1]/totalVal*100:0;
                p1.rect(M, sy-catRH, CW, catRH, ci%2==0?ROWA:ROWB);
                String cc=catCols[ci%catCols.length];
                p1.circle(M+10, sy-catRH/2, 4, cc);
                p1.text(M+20,  sy-catRH+5, e.getKey(),                               8, false, TXT);
                p1.text(M+160, sy-catRH+5, String.valueOf((int)e.getValue()[0]),      8, false, TXT);
                p1.text(M+220, sy-catRH+5, "Rs. "+String.format("%,.0f",e.getValue()[1]), 8, false, TXT);
                // Progress bar
                float barX=M+370, barW=100;
                p1.rrect(barX, sy-catRH+5, barW, 6, 3, BRDR);
                float fill=(float)(barW*pct/100); if(fill<2) fill=2;
                p1.rrect(barX, sy-catRH+5, fill, 6, 3, cc);
                p1.text(barX+barW+4, sy-catRH+5, String.format("%.0f%%",pct), 7, true, MUT);
                sy -= catRH; ci++;
            }
            p1.line(M,sy,M+CW,sy,BRDR,0.5f);

            // ── Footer P1 ──────────────────────────────────────────────────
            p1.rect(0, 0, PW, 22, rgb(12,12,30));
            p1.rect(0, 0, PW*0.5f, 2, ACC);
            p1.rect(PW*0.5f, 0, PW*0.5f, 2, BLU);
            p1.text(M,    10, "FAST  —  Fixed Asset Tracking System   |   MJ-IRP 2025-26   |   CONFIDENTIAL", 7, false, MUT);
            p1.text(PW-M-50, 10, "Page 1 of 2", 7, true, ACC);

            // ═══════════════════════════════════════════════════════════════
            // PAGE 2 CONTENT STREAM
            // ═══════════════════════════════════════════════════════════════
            PageStream p2 = new PageStream();
            p2.rect(0,0,PW,PH,BG);
            p2.rect(0,PH-60,PW,60, rgb(20,16,60));
            p2.rect(0,PH-5,PW*0.33f,5,ACC);
            p2.rect(PW*0.33f,PH-5,PW*0.34f,5,rgb(90,118,247));
            p2.rect(PW*0.67f,PH-5,PW*0.34f,5,BLU);
            p2.rect(0,0,3,PH,ACC);

            float sy2 = PH-18;

            // Section 3
            p2.rrect(M, sy2-20, CW, 20, 4, rgb(15,30,80));
            p2.rect(M, sy2-20, 4, 20, BLU);
            p2.text(M+10, sy2-14, "SECTION 3   CURRENTLY DEPLOYED ASSETS", 10, true, WHT);
            sy2 -= 22;

            p2.rect(M, sy2-13, CW, 13, HDRB);
            p2.text(M+8,   sy2-9, "Asset Name",  7, true, MUT);
            p2.text(M+165, sy2-9, "Assigned To", 7, true, MUT);
            p2.text(M+315, sy2-9, "Department",  7, true, MUT);
            p2.text(M+430, sy2-9, "Date",         7, true, MUT);
            sy2 -= 13;

            if(active.isEmpty()){
                p2.rect(M,sy2-RH,CW,RH,ROWA);
                p2.text(M+8,sy2-RH+5,"No assets currently deployed.",8,false,MUT);
                sy2-=RH;
            } else {
                int ai=0;
                for(Assignment a:active){
                    String an="Unknown", en="Unassigned", ed="—";
                    for(Asset x:assets){if(x.getAssetId()==a.getAssetId()){an=x.getAssetName();break;}}
                    for(Employee e:emps){if(e.getEmployeeId()==a.getEmployeeId()){en=e.getEmployeeName();ed=e.getDepartment();break;}}
                    p2.rect(M,sy2-RH,CW,RH,ai%2==0?ROWA:ROWB);
                    p2.circle(M+8,sy2-RH/2,4,GRN);
                    p2.text(M+18, sy2-RH+5, an.length()>24?an.substring(0,24):an, 8,false,TXT);
                    p2.text(M+165,sy2-RH+5, en.length()>22?en.substring(0,22):en, 8,false,TXT);
                    p2.text(M+315,sy2-RH+5, ed.length()>18?ed.substring(0,18):ed, 8,false,TXT);
                    p2.text(M+430,sy2-RH+5, a.getAssignedDate(), 8,false,MUT);
                    sy2-=RH; ai++;
                }
            }
            p2.line(M,sy2,M+CW,sy2,BRDR,0.5f); sy2-=18;

            // Section 4: Full Inventory
            p2.rrect(M,sy2-20,CW,20,4,rgb(35,16,65));
            p2.rect(M,sy2-20,4,20,PUR);
            p2.text(M+10,sy2-14,"SECTION 4   FULL ASSET INVENTORY",10,true,WHT);
            sy2-=22;

            p2.rect(M,sy2-13,CW,13,HDRB);
            p2.text(M+6,   sy2-9,"ID",             7,true,MUT);
            p2.text(M+26,  sy2-9,"Asset Name",     7,true,MUT);
            p2.text(M+200, sy2-9,"Category",       7,true,MUT);
            p2.text(M+305, sy2-9,"Status",         7,true,MUT);
            p2.text(M+400, sy2-9,"Purchase Price", 7,true,MUT);
            sy2-=13;

            float invRH=15;
            int invBreakAt = -1;
            for(int i=0;i<assets.size();i++){
                if(sy2 < 180){ invBreakAt=i; break; }
                Asset a=assets.get(i);
                p2.rect(M,sy2-invRH,CW,invRH,i%2==0?ROWA:ROWB);
                String sc=MUT;
                for(int si=0;si<S_NAMES.length;si++) if(S_NAMES[si].equals(a.getStatus())){sc=S_COLS[si];break;}
                p2.circle(M+8,sy2-invRH/2,3.5f,sc);
                p2.text(M+17, sy2-invRH+4,String.valueOf(a.getAssetId()),  7,false,MUT);
                p2.text(M+28, sy2-invRH+4,a.getAssetName().length()>24?a.getAssetName().substring(0,24):a.getAssetName(), 7,false,TXT);
                p2.text(M+200,sy2-invRH+4,a.getCategory(), 7,false,TXT);
                p2.text(M+305,sy2-invRH+4,a.getStatus(),   7,true, sc);
                String pr="Rs. "+String.format("%,.0f",a.getPurchasePrice());
                float prw=pr.length()*4.8f;
                p2.text(M+CW-prw-6,sy2-invRH+4,pr,7,false,TXT);
                sy2-=invRH;
            }
            p2.line(M,sy2,M+CW,sy2,BRDR,0.5f); sy2-=18;

            // Section 5: Depreciation
            if(sy2>80){
                p2.rrect(M,sy2-20,CW,20,4,rgb(55,32,8));
                p2.rect(M,sy2-20,4,20,ORG);
                p2.text(M+10,sy2-14,"SECTION 5   DEPRECIATION SUMMARY  (Straight-Line  |  Salvage 10%)",10,true,WHT);
                sy2-=22;

                p2.rect(M,sy2-13,CW,13,HDRB);
                p2.text(M+8,   sy2-9,"Asset Name",      7,true,MUT);
                p2.text(M+195, sy2-9,"Purchase Price",  7,true,MUT);
                p2.text(M+295, sy2-9,"Life",             7,true,MUT);
                p2.text(M+355, sy2-9,"Annual Depr.",    7,true,MUT);
                p2.text(M+450, sy2-9,"Salvage",         7,true,MUT);
                sy2-=13;

                int depBreakAt=-1;
                for(int i=0;i<assets.size();i++){
                    if(sy2<30){depBreakAt=i;break;}
                    Asset a=assets.get(i);
                    String cat=a.getCategory().toLowerCase();
                    int life=cat.equals("electronics")||cat.equals("network")?3:cat.equals("vehicles")?5:7;
                    double salv=a.getPurchasePrice()*0.10;
                    double ann=(a.getPurchasePrice()-salv)/life;
                    p2.rect(M,sy2-invRH,CW,invRH,i%2==0?ROWA:ROWB);
                    p2.text(M+8,  sy2-invRH+4,a.getAssetName().length()>22?a.getAssetName().substring(0,22):a.getAssetName(),7,false,TXT);
                    p2.text(M+195,sy2-invRH+4,"Rs."+String.format("%,.0f",a.getPurchasePrice()),7,false,TXT);
                    p2.text(M+295,sy2-invRH+4,life+" yrs",7,false,TXT);
                    p2.text(M+355,sy2-invRH+4,"Rs."+String.format("%,.0f",ann),7,true,ORG);
                    p2.text(M+450,sy2-invRH+4,"Rs."+String.format("%,.0f",salv),7,false,MUT);
                    sy2-=invRH;
                }
            }

            // Footer P2
            p2.rect(0,0,PW,22,rgb(12,12,30));
            p2.rect(0,0,PW*0.5f,2,ACC);
            p2.rect(PW*0.5f,0,PW*0.5f,2,BLU);
            p2.text(M,   10,"FAST  —  Fixed Asset Tracking System   |   MJ-IRP 2025-26   |   CONFIDENTIAL",7,false,MUT);
            p2.text(PW-M-50,10,"Page 2 of 2",7,true,ACC);

            // ── Assemble PDF ───────────────────────────────────────────────
            // Font resources — Helvetica and Helvetica-Bold (built-in PDF fonts)
            String fontResources =
                "<</Font <</FR <</Type /Font /Subtype /Type1 /BaseFont /Helvetica>>" +
                "              /FB <</Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold>>>>>>";

            String cs1 = p1.get(), cs2 = p2.get();
            byte[] cs1b = cs1.getBytes("ISO-8859-1"), cs2b = cs2.getBytes("ISO-8859-1");

            // Object 1: Catalog
            objects.add("<</Type /Catalog /Pages 2 0 R>>");
            // Object 2: Pages
            objects.add("<</Type /Pages /Kids [3 0 R 5 0 R] /Count 2>>");
            // Object 3: Page 1
            objects.add("<</Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Contents 4 0 R /Resources "+fontResources+">>");
            // Object 4: Content stream P1 (added as stream below)
            // Object 5: Page 2
            objects.add("<</Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Contents 6 0 R /Resources "+fontResources+">>");
            // Object 6: Content stream P2 (added as stream below)

            // Write PDF
            write("%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n");

            int[] objOffsets = new int[7]; // 1-indexed

            // Obj 1: Catalog
            objOffsets[1]=pos;
            write("1 0 obj\n<</Type /Catalog /Pages 2 0 R>>\nendobj\n");

            // Obj 2: Pages
            objOffsets[2]=pos;
            write("2 0 obj\n<</Type /Pages /Kids [3 0 R 5 0 R] /Count 2>>\nendobj\n");

            // Obj 3: Page 1
            objOffsets[3]=pos;
            write("3 0 obj\n<</Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Contents 4 0 R /Resources "+fontResources+">>\nendobj\n");

            // Obj 4: Content stream Page 1
            objOffsets[4]=pos;
            write("4 0 obj\n<</Length "+cs1b.length+">>\nstream\n");
            byte[] cb1=cs1.getBytes("ISO-8859-1"); buf.write(cb1); pos+=cb1.length;
            write("\nendstream\nendobj\n");

            // Obj 5: Page 2
            objOffsets[5]=pos;
            write("5 0 obj\n<</Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Contents 6 0 R /Resources "+fontResources+">>\nendobj\n");

            // Obj 6: Content stream Page 2
            objOffsets[6]=pos;
            write("6 0 obj\n<</Length "+cs2b.length+">>\nstream\n");
            byte[] cb2=cs2.getBytes("ISO-8859-1"); buf.write(cb2); pos+=cb2.length;
            write("\nendstream\nendobj\n");

            // XRef
            int xrefPos=pos;
            write("xref\n0 7\n");
            write("0000000000 65535 f \n");
            for(int i=1;i<=6;i++) write(String.format("%010d 00000 n \n",objOffsets[i]));
            write("trailer\n<</Size 7 /Root 1 0 R>>\nstartxref\n"+xrefPos+"\n%%EOF\n");

            try(java.io.FileOutputStream fos=new java.io.FileOutputStream(fname)){
                buf.writeTo(fos);
            }
        }
    }







    //  EXPORT .TXT
    // ══════════════════════════════════════════════════════════════════════════
    private void exportReport() {
        String filename  = "Asset_Status_Report.txt";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<Asset>      allAssets = assetSvc.getAllAssets();
        List<Employee>   allEmp    = empSvc.getAllEmployees();
        List<Assignment> active    = assignSvc.getActiveAssignments();

        long avail = allAssets.stream().filter(a -> a.getStatus().equals("Available")).count();
        long asgn  = allAssets.stream().filter(a -> a.getStatus().equals("Assigned")).count();
        long maint = allAssets.stream().filter(a -> a.getStatus().equals("Maintenance")).count();
        long ret   = allAssets.stream().filter(a -> a.getStatus().equals("Retired")).count();
        double total = allAssets.stream().mapToDouble(Asset::getPurchasePrice).sum();

        ScrollPane scroll = pageScroll();
        VBox page = pagePad();
        page.getChildren().add(pageHeader("Export Report", "Text report saved to project directory"));

        try (PrintWriter w = new PrintWriter(new FileWriter(filename))) {
            w.println("=================================================");
            w.println("   FIXED ASSET TRACKING SYSTEM");
            w.println("   Muffakham Jah College of Engineering & Technology");
            w.println("   MJ-IRP | Academic Year 2025-26");
            w.println("   Generated: " + timestamp);
            w.println("=================================================");
            w.println(); w.println("SUMMARY");
            w.println("-------------------------------------------------");
            w.println("Total Assets       : " + allAssets.size());
            w.println("Total Value        : " + String.format("Rs. %,.2f", total));
            w.println("Available          : " + avail);
            w.println("Assigned           : " + asgn);
            w.println("Maintenance        : " + maint);
            w.println("Retired            : " + ret);
            w.println("Total Staff        : " + allEmp.size());
            w.println();
            w.println("CURRENTLY DEPLOYED ASSETS");
            w.println("-------------------------------------------------");
            if (active.isEmpty()) {
                w.println("No assets currently deployed.");
            } else {
                for (Assignment a : active) {
                    String an = allAssets.stream().filter(x -> x.getAssetId() == a.getAssetId())
                        .map(Asset::getAssetName).findFirst().orElse("Unknown");
                    String en = allEmp.stream().filter(x -> x.getEmployeeId() == a.getEmployeeId())
                        .map(Employee::getEmployeeName).findFirst().orElse("Unknown");
                    w.println("Asset    : " + an + "  |  Employee : " + en + "  |  Since : " + a.getAssignedDate());
                }
            }
            w.println(); w.println("FULL ASSET INVENTORY");
            w.println("-------------------------------------------------");
            allAssets.forEach(a -> w.println(a.toString()));
            w.println(); w.println("=================================================");
            w.println("END OF REPORT"); w.println("=================================================");

            // Success UI
            VBox successCard = new VBox(16);
            successCard.setAlignment(Pos.CENTER);
            successCard.setPadding(new Insets(50));
            successCard.setStyle("-fx-background-color: " + C_CARD + "; -fx-background-radius: 16;");
            Label icon = new Label("✅");
            icon.setFont(Font.font(52));
            Label msg  = new Label("Report Exported Successfully!");
            msg.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
            msg.setTextFill(Color.web(C_GREEN));
            Label pathLbl = new Label("📁  Saved as: " + filename);
            pathLbl.setFont(Font.font("Verdana", 13));
            pathLbl.setTextFill(Color.web(C_MUTED));
            Label statsLbl = new Label(
                "Assets: " + allAssets.size() + "   |   Available: " + avail +
                "   |   Assigned: " + asgn + "   |   Total Value: ₹" + String.format("%,.2f", total));
            statsLbl.setFont(Font.font("Verdana", 12));
            statsLbl.setTextFill(Color.web(C_MUTED));
            successCard.getChildren().addAll(icon, msg, pathLbl, statsLbl);
            page.getChildren().add(successCard);

        } catch (IOException e) {
            showAlert("Export failed: " + e.getMessage());
        }

        scroll.setContent(page);
        setContent(scroll);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CHARTS  (Canvas-based — no external lib needed)
    // ══════════════════════════════════════════════════════════════════════════

    /** Professional bar chart used by new dashboard */
    private void drawProfessionalBar(Canvas canvas, long avail, long asgn, long maint, long retired) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        double w = canvas.getWidth(), h = canvas.getHeight();
        double[] vals   = {avail, asgn, maint, retired};
        String[] labels = {"Available", "Assigned", "Maintenance", "Retired"};
        Color[]  colors = {Color.web(C_GREEN), Color.web(C_ACCENT2), Color.web(C_ORANGE), Color.web(C_MUTED)};
        double maxVal   = Math.max(1, Math.max(Math.max(avail, asgn), Math.max(maint, retired)));
        double barW = 72, gap = 34, startX = 50, baseY = h - 38;

        // Dashed horizontal grid lines
        for (int i = 1; i <= 4; i++) {
            double gy = baseY - (i * (h - 60) / 4.0);
            gc.setStroke(Color.web(C_BORDER + "99"));
            gc.setLineWidth(0.6);
            gc.setLineDashes(5, 5);
            gc.strokeLine(40, gy, w - 12, gy);
            gc.setLineDashes(0);
            gc.setFill(Color.web(C_MUTED));
            gc.setFont(Font.font("Verdana", 8));
            gc.fillText(String.valueOf((int)(maxVal * i / 4)), 4, gy + 4);
        }
        // X-axis baseline
        gc.setStroke(Color.web(C_BORDER));
        gc.setLineWidth(1); gc.setLineDashes(0);
        gc.strokeLine(40, baseY, w - 12, baseY);

        for (int i = 0; i < vals.length; i++) {
            double barH = vals[i] == 0 ? 3 : (vals[i] / maxVal) * (h - 60);
            double x = startX + i * (barW + gap);
            double y = baseY - barH;
            // Drop shadow
            gc.setFill(colors[i].deriveColor(0, 1, 0.5, 0.25));
            gc.fillRoundRect(x + 3, y + 5, barW, barH, 9, 9);
            // Gradient fill
            LinearGradient grad = new LinearGradient(0, y, 0, baseY, false, CycleMethod.NO_CYCLE,
                new Stop(0, colors[i]),
                new Stop(1, colors[i].deriveColor(0, 1, 0.55, 0.8)));
            gc.setFill(grad);
            gc.fillRoundRect(x, y, barW, barH, 9, 9);
            // Top shine
            gc.setFill(Color.web("#ffffff18"));
            gc.fillRoundRect(x + 6, y + 2, barW - 12, Math.min(barH - 2, 14), 6, 6);
            // Value on top
            gc.setFill(Color.web(C_TEXT));
            gc.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
            String v = String.valueOf((long) vals[i]);
            gc.fillText(v, x + barW / 2 - v.length() * 3.5, y - 7);
            // Dot + label below
            gc.setFill(colors[i]);
            gc.fillOval(x + barW / 2 - 4, baseY + 5, 8, 8);
            gc.setFill(Color.web(C_MUTED));
            gc.setFont(Font.font("Verdana", 9));
            gc.fillText(labels[i], x + barW / 2 - labels[i].length() * 3.2, baseY + 26);
        }
    }

    /** Professional donut chart — FIXED segment overlap using ArcType.OPEN + manual cutout */
    private void drawProfessionalDonut(Canvas canvas, long avail, long asgn, long maint, long retired) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        double w = canvas.getWidth(), h = canvas.getHeight();
        long total = avail + asgn + maint + retired;

        if (total == 0) {
            gc.setFill(Color.web(C_MUTED));
            gc.setFont(Font.font("Verdana", 11));
            gc.fillText("No data yet", w / 2 - 36, h / 2);
            return;
        }

        double cx = w / 2, cy = (h - 55) / 2 + 8;
        double outerR = 76, innerR = 46;

        long[]   vals   = {avail, asgn, maint, retired};
        Color[]  colors = {Color.web(C_GREEN), Color.web(C_ACCENT2), Color.web(C_ORANGE), Color.web(C_MUTED)};
        String[] lbls   = {"Available", "Assigned", "Maint.", "Retired"};
        double   gap    = 2.0; // visual gap in degrees

        // ── STEP 1: draw full pie slices (fillArc ROUND = pie slice to center) ─
        double angle = 90.0; // JavaFX: 0=right, 90=top, goes counter-clockwise
        for (int i = 0; i < vals.length; i++) {
            if (vals[i] == 0) { continue; }
            double sweep = (vals[i] / (double) total) * 360.0;
            // Draw slightly smaller sweep to create gap effect
            gc.setFill(colors[i]);
            gc.fillArc(
                cx - outerR, cy - outerR, outerR * 2, outerR * 2,
                angle + gap / 2,
                sweep - gap,
                javafx.scene.shape.ArcType.ROUND
            );
            angle += sweep;
        }

        // ── STEP 2: stamp center circle with background color = creates donut hole
        gc.setFill(Color.web(C_CARD));
        gc.fillOval(cx - innerR, cy - innerR, innerR * 2, innerR * 2);

        // ── STEP 3: center text ───────────────────────────────────────────────
        gc.setFill(Color.web(C_TEXT));
        gc.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        String tot = String.valueOf(total);
        double tw = tot.length() * 7.5;
        gc.fillText(tot, cx - tw / 2, cy + 8);
        gc.setFill(Color.web(C_MUTED));
        gc.setFont(Font.font("Verdana", 8));
        gc.fillText("assets", cx - 14, cy + 21);

        // ── STEP 4: legend 2×2 below chart ────────────────────────────────────
        double legY = cy + outerR + 12;
        for (int i = 0; i < vals.length; i++) {
            double lx = 10 + (i % 2) * 124;
            double ly = legY + (i / 2) * 18;
            double pct = vals[i] / (double) total * 100;
            gc.setFill(colors[i]);
            gc.fillRoundRect(lx, ly, 9, 9, 3, 3);
            gc.setFill(Color.web(C_MUTED));
            gc.setFont(Font.font("Verdana", 8));
            gc.fillText(String.format("%s  %.0f%%", lbls[i], pct), lx + 14, ly + 8);
        }
    }

    private void drawEnhancedBarChart(Canvas canvas, long avail, long asgn, long maint, long retired) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        double w = canvas.getWidth(), h = canvas.getHeight();
        double maxVal = Math.max(1, Math.max(Math.max(avail, asgn), Math.max(maint, retired)));
        double barW = 80, gap = 30, startX = 52;
        double[] vals   = {avail, asgn, maint, retired};
        String[] labels = {"Available", "Assigned", "Maintenance", "Retired"};
        String[] hexCol = {C_GREEN, C_ACCENT2, C_ORANGE, C_MUTED};
        Color[]  colors = {Color.web(C_GREEN), Color.web(C_ACCENT2), Color.web(C_ORANGE), Color.web(C_MUTED)};

        // Subtle horizontal grid lines
        gc.setStroke(Color.web(C_BORDER + "88"));
        gc.setLineWidth(0.8);
        int gridLines = 5;
        for (int i = 1; i <= gridLines; i++) {
            double y = h - 36 - (i * (h - 60) / (double) gridLines);
            gc.setLineDashes(4, 4);
            gc.strokeLine(42, y, w - 10, y);
            gc.setLineDashes(0);
            gc.setFill(Color.web(C_MUTED));
            gc.setFont(Font.font("Verdana", 8));
            gc.fillText(String.valueOf((int)(maxVal * i / gridLines)), 4, y + 4);
        }

        // X axis line
        gc.setStroke(Color.web(C_BORDER));
        gc.setLineWidth(1);
        gc.strokeLine(42, h - 36, w - 10, h - 36);

        for (int i = 0; i < vals.length; i++) {
            double barH = vals[i] == 0 ? 3 : (vals[i] / maxVal) * (h - 60);
            double x    = startX + i * (barW + gap);
            double y    = h - 36 - barH;

            // Glow shadow under bar
            gc.setFill(Color.web(hexCol[i] + "33"));
            gc.fillRoundRect(x + 4, y + 4, barW, barH, 10, 10);

            // Gradient bar
            LinearGradient lg = new LinearGradient(0, y, 0, h - 36, false, CycleMethod.NO_CYCLE,
                new Stop(0, colors[i]),
                new Stop(0.7, colors[i].deriveColor(0, 1, 0.75, 0.9)),
                new Stop(1,   colors[i].deriveColor(0, 1, 0.45, 0.7)));
            gc.setFill(lg);
            gc.fillRoundRect(x, y, barW, barH, 10, 10);

            // Shimmer top highlight
            LinearGradient shine = new LinearGradient(x, 0, x + barW, 0, false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#ffffff00")),
                new Stop(0.4, Color.web("#ffffff22")),
                new Stop(1, Color.web("#ffffff00")));
            gc.setFill(shine);
            gc.fillRoundRect(x, y, barW, Math.min(barH, 20), 10, 10);

            // Value label above bar
            gc.setFill(Color.web(C_TEXT));
            gc.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
            String valStr = String.valueOf((long) vals[i]);
            gc.fillText(valStr, x + barW / 2 - (valStr.length() * 4), y - 8);

            // Colored dot + label below axis
            gc.setFill(colors[i]);
            gc.fillOval(x + barW / 2 - 4, h - 28, 8, 8);
            gc.setFill(Color.web(C_MUTED));
            gc.setFont(Font.font("Verdana", 9));
            double lx = x + barW / 2 - (labels[i].length() * 3.2);
            gc.fillText(labels[i], lx, h - 10);
        }
    }

    private void drawEnhancedDonut(Canvas canvas, long avail, long asgn,
                                    long maint, long retired, int total) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        double w = canvas.getWidth(), h = canvas.getHeight();
        double sum = avail + asgn + maint + retired;
        if (sum == 0) {
            gc.setFill(Color.web(C_MUTED));
            gc.setFont(Font.font("Verdana", 11));
            gc.fillText("No data yet", w / 2 - 35, h / 2);
            return;
        }

        double cx = w / 2, cy = h / 2 - 8;
        double outerR = 90, innerR = 54;
        double[] vals   = {avail, asgn, maint, retired};
        Color[]  colors = {Color.web(C_GREEN), Color.web(C_ACCENT2), Color.web(C_ORANGE), Color.web(C_MUTED)};
        String[] lbls   = {"Available", "Assigned", "Maint.", "Retired"};

        // Draw segments with gaps
        double angle = -90;
        double gapDeg = 2.5;
        for (int i = 0; i < vals.length; i++) {
            if (vals[i] == 0) continue;
            double sweep = (vals[i] / sum) * 360 - gapDeg;

            // Outer glow
            gc.setFill(colors[i].deriveColor(0, 1, 1, 0.15));
            gc.fillArc(cx - outerR - 6, cy - outerR - 6,
                (outerR + 6) * 2, (outerR + 6) * 2,
                angle, sweep, javafx.scene.shape.ArcType.ROUND);

            // Main segment
            gc.setFill(colors[i]);
            gc.fillArc(cx - outerR, cy - outerR, outerR * 2, outerR * 2,
                angle, sweep, javafx.scene.shape.ArcType.ROUND);

            angle += sweep + gapDeg;
        }

        // Inner hollow
        gc.setFill(Color.web(C_CARD));
        gc.fillOval(cx - innerR, cy - innerR, innerR * 2, innerR * 2);

        // Center: total count
        gc.setFill(Color.web(C_TEXT));
        gc.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        String totalStr = String.valueOf(total);
        gc.fillText(totalStr, cx - (totalStr.length() * 7), cy + 8);
        gc.setFont(Font.font("Verdana", 9));
        gc.setFill(Color.web(C_MUTED));
        gc.fillText("assets", cx - 16, cy + 22);

        // Legend — 2 per row
        double legY = h - 44;
        double legX = 8;
        for (int i = 0; i < vals.length; i++) {
            if (i == 2) { legX = 8; legY += 20; }
            double pct = sum == 0 ? 0 : vals[i] / sum * 100;
            gc.setFill(colors[i]);
            gc.fillRoundRect(legX, legY, 10, 10, 3, 3);
            gc.setFill(Color.web(C_MUTED));
            gc.setFont(Font.font("Verdana", 8));
            gc.fillText(lbls[i] + " " + String.format("%.0f%%", pct), legX + 13, legY + 9);
            legX += 108;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UI COMPONENT HELPERS
    // ══════════════════════════════════════════════════════════════════════════
    private void setContent(javafx.scene.Node node) {
        contentStack.getChildren().setAll(node);
    }

    private ScrollPane pageScroll() {
        ScrollPane sp = new ScrollPane();
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: " + C_BG + "; -fx-background-color: " + C_BG + ";");
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }

    private VBox pagePad() {
        VBox v = new VBox(22);
        v.setPadding(new Insets(32, 36, 36, 36));
        v.setStyle("-fx-background-color: " + C_BG + ";");
        return v;
    }

    private HBox pageHeader(String title, String subtitle) {
        VBox text = new VBox(4);
        Label t = new Label(title);
        t.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        t.setTextFill(Color.web(C_TEXT));
        Label s = new Label(subtitle);
        s.setFont(Font.font("Verdana", 12));
        s.setTextFill(Color.web(C_MUTED));
        text.getChildren().addAll(t, s);

        // Accent line
        Region line = new Region();
        line.setPrefWidth(4);
        line.setPrefHeight(48);
        line.setStyle("-fx-background-color: linear-gradient(to bottom, " + C_ACCENT + ", " + C_PURPLE + ");" +
            "-fx-background-radius: 2;");

        HBox header = new HBox(14, line, text);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private VBox sectionCard(String title) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20, 22, 20, 22));
        card.setStyle(
            "-fx-background-color: " + C_CARD + ";" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: " + C_BORDER + ";" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;");
        DropShadow ds = new DropShadow(12, Color.web("#00000055"));
        card.setEffect(ds);
        Label lbl = new Label(title);
        lbl.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web(C_TEXT));
        card.getChildren().add(lbl);
        return card;
    }

    private VBox chartCard(String title) {
        VBox card = sectionCard(title);
        card.setPadding(new Insets(16, 18, 16, 18));
        return card;
    }

    private VBox kpiCard(String title, String value, String icon, String color, String sub) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setPrefWidth(195);
        card.setMinWidth(160);
        card.setStyle(
            "-fx-background-color: " + C_CARD + ";" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: " + color + "44;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;");

        Label ico = new Label(icon);
        ico.setFont(Font.font(20));

        Label val = new Label(value);
        val.setFont(Font.font("Georgia", FontWeight.BOLD, 32));
        val.setTextFill(Color.web(color));

        Label ttl = new Label(title);
        ttl.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        ttl.setTextFill(Color.web(C_TEXT));

        Label s = new Label(sub);
        s.setFont(Font.font("Verdana", 9));
        s.setTextFill(Color.web(C_MUTED));

        card.getChildren().addAll(ico, val, ttl, s);

        // Animate count-up
        final int target;
        try { target = Integer.parseInt(value); } catch (Exception e) { return card; }
        Timeline tl = new Timeline();
        tl.getKeyFrames().add(new KeyFrame(Duration.millis(800), ev -> {}));
        IntegerProperty counter = new SimpleIntegerProperty(0);
        counter.addListener((obs, ov, nv) -> val.setText(String.valueOf(nv.intValue())));
        Timeline count = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(counter, 0)),
            new KeyFrame(Duration.millis(600), new KeyValue(counter, target))
        );
        count.play();
        return card;
    }

    private VBox categoryBar(String label, long count, double pct, String color) {
        VBox col = new VBox(6);
        col.setAlignment(Pos.BOTTOM_CENTER);
        col.setPrefWidth(80);

        double maxH = 130;
        double barH = Math.max(8, pct * maxH);
        Region bar = new Region();
        bar.setPrefWidth(44);
        bar.setPrefHeight(barH);
        bar.setStyle(
            "-fx-background-color: linear-gradient(to top, " + color + "88, " + color + ");" +
            "-fx-background-radius: 6 6 0 0;");

        Label cnt = new Label(String.valueOf(count));
        cnt.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        cnt.setTextFill(Color.web(color));
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Verdana", 9));
        lbl.setTextFill(Color.web(C_MUTED));
        lbl.setWrapText(true);
        lbl.setMaxWidth(76);
        lbl.setAlignment(Pos.CENTER);

        col.getChildren().addAll(cnt, bar, lbl);
        return col;
    }

    private <T> TableView<T> buildTable() {
        TableView<T> tbl = new TableView<>();
        tbl.setPrefHeight(300);
        tbl.setStyle(
            "-fx-background-color: " + C_SURFACE + ";" +
            "-fx-border-color: " + C_BORDER + ";" +
            "-fx-border-radius: 8;");
        tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        return tbl;
    }

    // Columns are declared inline with explicit types to avoid generics conflicts

    private void addAssignCols(TableView<Assignment> tbl) {
        TableColumn<Assignment, Integer> c1 = new TableColumn<>("ID");
        TableColumn<Assignment, Integer> c2 = new TableColumn<>("Asset ID");
        TableColumn<Assignment, Integer> c3 = new TableColumn<>("Employee ID");
        TableColumn<Assignment, String>  c4 = new TableColumn<>("Assigned Date");
        TableColumn<Assignment, String>  c5 = new TableColumn<>("Return Date");
        TableColumn<Assignment, String>  c6 = new TableColumn<>("Status");
        c1.setPrefWidth(55); c2.setPrefWidth(85); c3.setPrefWidth(105);
        c4.setPrefWidth(130); c5.setPrefWidth(130); c6.setPrefWidth(100);
        c1.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getAssignmentId()).asObject());
        c2.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getAssetId()).asObject());
        c3.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getEmployeeId()).asObject());
        c4.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAssignedDate()));
        c5.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReturnDate()));
        c6.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        statusColorCellAssign(c6);
        tbl.getColumns().addAll(c1, c2, c3, c4, c5, c6);
    }

    private void statusColorCell(TableColumn<Asset, String> col) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                String color;
                if      ("Available".equals(item))   color = C_GREEN;
                else if ("Assigned".equals(item))    color = C_ACCENT2;
                else if ("Maintenance".equals(item)) color = C_ORANGE;
                else if ("Retired".equals(item))     color = C_MUTED;
                else                                 color = C_TEXT;
                setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
            }
        });
    }

    private void statusColorCellAssign(TableColumn<Assignment, String> col) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("-fx-text-fill: " + (item.equals("Assigned") ? C_ACCENT2 : C_GREEN) +
                    "; -fx-font-weight: bold;");
            }
        });
    }

    private TableView<String[]> rawTable() {
        TableView<String[]> tbl = new TableView<>();
        tbl.setPrefHeight(220);
        tbl.setStyle(
            "-fx-background-color: " + C_SURFACE + ";" +
            "-fx-border-color: " + C_BORDER + ";" +
            "-fx-border-radius: 8;");
        tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        return tbl;
    }

    private TableColumn<String[], String> rawCol(String title, int idx, int width) {
        TableColumn<String[], String> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().length > idx ? d.getValue()[idx] : ""));
        return col;
    }

    private GridPane buildGrid() {
        GridPane gp = new GridPane();
        gp.setHgap(18); gp.setVgap(12);
        gp.setPadding(new Insets(4, 0, 4, 0));
        ColumnConstraints c0 = new ColumnConstraints(130);
        ColumnConstraints c1 = new ColumnConstraints(280);
        gp.getColumnConstraints().addAll(c0, c1);
        return gp;
    }

    private void addStatRow(GridPane gp, int row, String label, String value) {
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Verdana", 12));
        lbl.setTextFill(Color.web(C_MUTED));
        Label val = new Label(value);
        val.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        val.setTextFill(Color.web(C_TEXT));
        gp.add(lbl, 0, row);
        gp.add(val, 1, row);
    }

    private TextField field(String id, String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(260);
        tf.setStyle(
            "-fx-background-color: " + C_SURFACE + ";" +
            "-fx-text-fill: " + C_TEXT + ";" +
            "-fx-prompt-text-fill: " + C_MUTED + ";" +
            "-fx-border-color: " + C_BORDER + ";" +
            "-fx-border-radius: 7; -fx-background-radius: 7;" +
            "-fx-padding: 9 12;");
        tf.focusedProperty().addListener((obs, ov, nv) -> {
            if (nv) tf.setStyle(tf.getStyle().replace(C_BORDER, C_ACCENT));
            else    tf.setStyle(tf.getStyle().replace(C_ACCENT, C_BORDER));
        });
        return tf;
    }

    private TextField searchField(String prompt) {
        TextField tf = field("", prompt);
        return tf;
    }

    private <T> ComboBox<String> styledCombo(String... items) {
        ComboBox<String> cb = new ComboBox<>(FXCollections.observableArrayList(items));
        cb.setValue(items[0]);
        cb.setStyle(
            "-fx-background-color: " + C_SURFACE + ";" +
            "-fx-text-fill: " + C_TEXT + ";" +
            "-fx-border-color: " + C_BORDER + ";" +
            "-fx-border-radius: 7; -fx-background-radius: 7;");
        return cb;
    }

    private Label flabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Verdana", 12));
        lbl.setTextFill(Color.web(C_MUTED));
        return lbl;
    }

    private Label mutedLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Verdana", 12));
        l.setTextFill(Color.web(C_MUTED));
        return l;
    }

    private Button accentBtn(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setStyle(
            "-fx-background-color: " + C_ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 9 18;");
        return btn;
    }

    private Button ghostBtn(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Verdana", 11));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + C_MUTED + ";" +
            "-fx-border-color: " + C_BORDER + ";" +
            "-fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-padding: 9 18;");
        return btn;
    }

    private Button warningBtn(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setStyle(
            "-fx-background-color: " + C_ORANGE + "22;" +
            "-fx-text-fill: " + C_ORANGE + ";" +
            "-fx-border-color: " + C_ORANGE + "66;" +
            "-fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-padding: 9 18;");
        return btn;
    }

    private Button dangerBtn(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setStyle(
            "-fx-background-color: " + C_ACCENT2 + "22;" +
            "-fx-text-fill: " + C_ACCENT2 + ";" +
            "-fx-border-color: " + C_ACCENT2 + "66;" +
            "-fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-padding: 9 18;");
        return btn;
    }

    private void setMsg(Label lbl, String text, String color) {
        lbl.setText(text);
        lbl.setTextFill(Color.web(color.isEmpty() ? C_TEXT : color));
        if (!text.isEmpty()) {
            FadeTransition ft = new FadeTransition(Duration.millis(3500), lbl);
            ft.setFromValue(1); ft.setToValue(0);
            ft.setDelay(Duration.millis(2000));
            ft.play();
        }
    }

    private void clearFields(TextField... fields) {
        for (TextField f : fields) f.clear();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.getDialogPane().setStyle("-fx-background-color: " + C_CARD + ";");
        alert.show();
    }

    public static void main(String[] args) { launch(args); }
}
