import os
import sys

def build_pdf(filepath):
    PAGE_WIDTH = 595.28
    PAGE_HEIGHT = 841.89
    MARGIN_LEFT = 50.0
    MARGIN_RIGHT = 50.0
    MARGIN_TOP = 55.0
    MARGIN_BOTTOM = 55.0
    USABLE_WIDTH = PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT

    pages_content = []
    current_stream = []
    current_y = PAGE_HEIGHT - MARGIN_TOP

    def start_new_page():
        nonlocal current_y, current_stream
        if current_stream:
            pages_content.append("\n".join(current_stream))
        current_stream = []
        current_y = PAGE_HEIGHT - MARGIN_TOP
        
        # Subtle top header band
        current_stream.append("q")
        current_stream.append("0.12 0.16 0.22 rg")
        current_stream.append(f"0 {PAGE_HEIGHT - 30} {PAGE_WIDTH} 30 re f")
        current_stream.append("1 1 1 rg")
        current_stream.append("BT")
        current_stream.append("/F2 9 Tf")
        current_stream.append(f"50 {PAGE_HEIGHT - 20} Td")
        current_stream.append("(SENTINEL AI  |  TECHNICAL & ALGORITHMIC SPECIFICATION REPORT) Tj")
        current_stream.append("ET")
        current_stream.append("Q")

        # Bottom footer
        current_stream.append("q")
        current_stream.append("0.6 0.6 0.6 RG 0.5 w")
        current_stream.append(f"50 40 m {PAGE_WIDTH - 50} 40 l S")
        current_stream.append("0.4 0.4 0.4 rg")
        current_stream.append("BT")
        current_stream.append("/F1 8 Tf")
        current_stream.append("50 28 Td")
        current_stream.append("(Confidential & Proprietary  -  Tamil Nadu Police Control Room 1091 / Singappen Task Force) Tj")
        current_stream.append("ET")
        current_stream.append("Q")
        
        current_y = PAGE_HEIGHT - 55

    def escape_text(text):
        return text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)")

    def add_title(text, subtitle=""):
        nonlocal current_y
        current_stream.append("q")
        current_stream.append("0.85 0.12 0.25 rg") # Crimson
        current_stream.append(f"50 {current_y - 28} 5 32 re f")
        current_stream.append("0.1 0.12 0.18 rg")
        current_stream.append("BT")
        current_stream.append("/F2 18 Tf")
        current_stream.append(f"62 {current_y - 20} Td")
        current_stream.append(f"({escape_text(text)}) Tj")
        current_stream.append("ET")
        current_stream.append("Q")
        current_y -= 36

        if subtitle:
            current_stream.append("q")
            current_stream.append("0.4 0.45 0.55 rg")
            current_stream.append("BT")
            current_stream.append("/F1 9.5 Tf")
            current_stream.append(f"62 {current_y} Td")
            current_stream.append(f"({escape_text(subtitle)}) Tj")
            current_stream.append("ET")
            current_stream.append("Q")
            current_y -= 20

    def add_heading_1(text):
        nonlocal current_y
        if current_y < 120:
            start_new_page()
        current_y -= 12
        current_stream.append("q")
        current_stream.append("0.94 0.96 0.98 rg")
        current_stream.append(f"50 {current_y - 4} {USABLE_WIDTH} 20 re f")
        current_stream.append("0.85 0.12 0.25 rg")
        current_stream.append(f"50 {current_y - 4} 3 20 re f")
        current_stream.append("0.12 0.16 0.24 rg")
        current_stream.append("BT")
        current_stream.append("/F2 11 Tf")
        current_stream.append(f"58 {current_y + 2} Td")
        current_stream.append(f"({escape_text(text)}) Tj")
        current_stream.append("ET")
        current_stream.append("Q")
        current_y -= 16

    def add_heading_2(text):
        nonlocal current_y
        if current_y < 90:
            start_new_page()
        current_y -= 6
        current_stream.append("q")
        current_stream.append("0.2 0.3 0.45 rg")
        current_stream.append("BT")
        current_stream.append("/F2 10 Tf")
        current_stream.append(f"50 {current_y} Td")
        current_stream.append(f"({escape_text(text)}) Tj")
        current_stream.append("ET")
        current_stream.append("Q")
        current_y -= 13

    def add_paragraph(text, indent=0, color=(0.15, 0.15, 0.18), size=9.0, is_bold=False):
        nonlocal current_y
        words = text.split()
        lines = []
        curr_line = []
        max_chars = int((USABLE_WIDTH - indent) / (size * 0.52))

        for w in words:
            if len(" ".join(curr_line + [w])) <= max_chars:
                curr_line.append(w)
            else:
                lines.append(" ".join(curr_line))
                curr_line = [w]
        if curr_line:
            lines.append(" ".join(curr_line))

        font_name = "/F2" if is_bold else "/F1"
        line_height = size * 1.32

        for line in lines:
            if current_y < MARGIN_BOTTOM + 20:
                start_new_page()
            current_stream.append("q")
            current_stream.append(f"{color[0]} {color[1]} {color[2]} rg")
            current_stream.append("BT")
            current_stream.append(f"{font_name} {size} Tf")
            current_stream.append(f"{MARGIN_LEFT + indent} {current_y} Td")
            current_stream.append(f"({escape_text(line)}) Tj")
            current_stream.append("ET")
            current_stream.append("Q")
            current_y -= line_height

        current_y -= 2

    def add_callout(box_title, items, border_color=(0.18, 0.65, 0.42), bg_color=(0.95, 0.98, 0.96)):
        nonlocal current_y
        item_lines_count = sum(max(1, len(it) // 75 + 1) for it in items)
        box_h = 20 + (item_lines_count * 12) + 6
        if current_y - box_h < MARGIN_BOTTOM:
            start_new_page()

        y_top = current_y
        current_stream.append("q")
        current_stream.append(f"{bg_color[0]} {bg_color[1]} {bg_color[2]} rg")
        current_stream.append(f"50 {y_top - box_h} {USABLE_WIDTH} {box_h} re f")
        current_stream.append(f"{border_color[0]} {border_color[1]} {border_color[2]} RG 1 w")
        current_stream.append(f"50 {y_top - box_h} {USABLE_WIDTH} {box_h} re S")
        current_stream.append(f"{border_color[0]} {border_color[1]} {border_color[2]} rg")
        current_stream.append(f"50 {y_top - box_h} 4 {box_h} re f")
        
        current_stream.append(f"{border_color[0]} {border_color[1]} {border_color[2]} rg")
        current_stream.append("BT")
        current_stream.append("/F2 9.5 Tf")
        current_stream.append(f"60 {y_top - 14} Td")
        current_stream.append(f"({escape_text(box_title)}) Tj")
        current_stream.append("ET")
        current_stream.append("Q")

        curr_box_y = y_top - 26
        for it in items:
            current_stream.append("q")
            current_stream.append("0.15 0.18 0.22 rg")
            current_stream.append("BT")
            current_stream.append("/F1 8.5 Tf")
            current_stream.append(f"62 {curr_box_y} Td")
            current_stream.append(f"({escape_text(it)}) Tj")
            current_stream.append("ET")
            current_stream.append("Q")
            curr_box_y -= 12

        current_y -= (box_h + 8)

    def add_table(headers, rows, col_widths):
        nonlocal current_y
        row_h = 16
        table_h = (len(rows) + 1) * row_h
        if current_y - table_h < MARGIN_BOTTOM:
            start_new_page()

        # Header Row
        current_stream.append("q")
        current_stream.append("0.15 0.20 0.28 rg")
        current_stream.append(f"50 {current_y - row_h} {USABLE_WIDTH} {row_h} re f")
        
        col_x = 55.0
        current_stream.append("1 1 1 rg")
        current_stream.append("BT")
        current_stream.append("/F2 8.0 Tf")
        for i, h in enumerate(headers):
            current_stream.append(f"{col_x} {current_y - 11} Td")
            current_stream.append(f"({escape_text(h)}) Tj")
            current_stream.append(f"-{col_x} -{current_y - 11} Td")
            col_x += col_widths[i]
        current_stream.append("ET")
        current_stream.append("Q")
        current_y -= row_h

        # Data Rows
        for r_idx, r in enumerate(rows):
            bg = (0.97, 0.98, 0.99) if r_idx % 2 == 0 else (1.0, 1.0, 1.0)
            current_stream.append("q")
            current_stream.append(f"{bg[0]} {bg[1]} {bg[2]} rg")
            current_stream.append(f"50 {current_y - row_h} {USABLE_WIDTH} {row_h} re f")
            current_stream.append("0.85 0.88 0.92 RG 0.5 w")
            current_stream.append(f"50 {current_y - row_h} m {50 + USABLE_WIDTH} {current_y - row_h} l S")

            col_x = 55.0
            current_stream.append("0.15 0.18 0.22 rg")
            current_stream.append("BT")
            current_stream.append("/F1 8.0 Tf")
            for i, val in enumerate(r):
                current_stream.append(f"{col_x} {current_y - 11} Td")
                current_stream.append(f"({escape_text(str(val))}) Tj")
                current_stream.append(f"-{col_x} -{current_y - 11} Td")
                col_x += col_widths[i]
            current_stream.append("ET")
            current_stream.append("Q")
            current_y -= row_h

        current_y -= 8

    # ==================== PAGE 1 ====================
    start_new_page()
    add_title("SENTINEL AI: DETECTION ALGORITHMS BLUEPRINT", "Deterministic Multi-Modal Sensor Fusion & Veto Architecture Specification")
    
    add_paragraph("AUTHOR: Principal Mobile Systems Architect & Signal Processing Team", color=(0.4, 0.45, 0.55), size=8.5, is_bold=True)
    add_paragraph("INTEGRATION: Singappen Women Safety Task Force / Tamil Nadu State Police Control Room 1091", color=(0.4, 0.45, 0.55), size=8.5)
    current_y -= 6

    add_heading_1("1. EXECUTIVE SUMMARY & PROBLEM CONTEXT")
    add_paragraph("Traditional emergency response systems (such as the default Android 5-power-button SOS or standalone acoustic scream monitors) suffer from catastrophic false-positive rates exceeding 90%. Overwhelming volumes of accidental pocket dials (caused by key compression inside bags or tight denim) and false alarms triggered by entertainment audio (screams in television movies or video games) frequently overwhelm police dispatchers.")
    add_paragraph("The Sentinel AI 'Cross-Modal SOS Veto Engine' introduces an intelligent, deterministic software gate. Rather than continuously draining battery life by sampling 24/7, the system remains dormant until an interrupt occurs (such as 5 rapid power-button presses or an amplitude spike). It then captures a synchronized 2,000-millisecond multi-sensor snapshot across three physical modalities, applies deterministic signal processing equations, and evaluates whether to ASSERT VETO (cancel dispatch) or CONFIRM DISPATCH (connect directly to Helpline 1091).")

    add_heading_1("2. THE THREE SENSOR MODALITIES (PHYSICAL REASONING)")
    add_paragraph("To guarantee defensibility under real-world physical constraints and patent scrutiny, the engine relies on three distinct physical sensor domains:")
    
    add_callout(
        "Modality 1: Kinetic Inertial Motion (Accelerometer + Gyroscope)",
        [
            "- Measures physical force, impact dynamics, and spatial rotational variance.",
            "- Distinguishes smooth walking rhythm or table rest from violent physical struggle.",
            "- Key Metric: Vector Jerk J = da/dt in g/s and angular variance sigma_omega^2 in rad/s^2."
        ],
        border_color=(0.85, 0.12, 0.25), bg_color=(0.99, 0.95, 0.96)
    )

    add_callout(
        "Modality 2: Optical Exposure Index (Proximity Sensor + Ambient Photometer)",
        [
            "- Identifies containment context: open atmosphere vs. trapped within dark enclosure.",
            "- Evaluates physical proximity distance (binary covered vs. far) and ambient lux levels.",
            "- Metric: O_containment = True when proximity is blocked and illuminance < 5.0 lux."
        ],
        border_color=(0.18, 0.65, 0.42), bg_color=(0.95, 0.98, 0.96)
    )

    add_callout(
        "Modality 3: Acoustic Playback & Harmonic Filter (16-bit PCM @ 16 kHz)",
        [
            "- Analyzes spectral harmonic dispersion to eliminate television and radio playback.",
            "- Leverages lossy compression rolloff: MP3/AAC codecs cut audio abruptly <= 15.0 kHz.",
            "- Live biological vocal tracts produce wideband distress harmonics extending > 16.0 kHz."
        ],
        border_color=(0.58, 0.20, 0.68), bg_color=(0.98, 0.95, 0.99)
    )

    # ==================== PAGE 2 ====================
    start_new_page()
    add_heading_1("3. CONCRETE MATHEMATICAL FORMULATIONS")
    add_paragraph("Every decision is mathematically computed in real time inside the 2,000ms capture window without remote cloud calls:")

    add_heading_2("A. Kinetic State Metric (K) & 3D Jerk Calculus")
    add_paragraph("Static acceleration magnitude (mean_g) cannot distinguish aggressive physical assault from someone jumping or running down stairs. Sentinel AI computes the Jerk Vector (J), representing the first time-derivative of 3-axis acceleration:")
    add_paragraph("Formula:  J(t) = d(a(t))/dt = lim [a(t + dt) - a(t)] / dt", indent=15, is_bold=True, color=(0.1, 0.2, 0.4))
    add_paragraph("Euclidean 3-Axis Jerk Magnitude:  |J| = sqrt( (dAx/dt)^2 + (dAy/dt)^2 + (dAz/dt)^2 )", indent=15, is_bold=True, color=(0.1, 0.2, 0.4))
    add_paragraph("Rotational Variance across N Gyroscope samples:  sigma_omega^2 = (1/N) * sum( (||omega_i|| - mean_omega)^2 )", indent=15, is_bold=True, color=(0.1, 0.2, 0.4))
    add_paragraph("- Resting Table Condition: |J| < 0.20 g/s and mean_g ~ 1.0 g.")
    add_paragraph("- Walking Rhythm: 0.20 g/s <= |J| < 2.0 g/s and mean_g < 1.5 g.")
    add_paragraph("- Violent Physical Struggle / Blows: |J| > 3.0 g/s and rotational variance > 3.5 rad/s^2.")

    add_heading_2("B. Optical Containment Index (O)")
    add_paragraph("Quantifies probability that the mobile device is trapped inside clothing or bag fabric:")
    add_paragraph("O_index = 1.0 if (Prox == Covered and Lux < 5.0) else (0.0 if Lux >= 20.0 else (20 - Lux)/20)", indent=15, is_bold=True, color=(0.1, 0.2, 0.4))

    add_heading_2("C. Acoustic Spectral Rolloff & Total Harmonic Distortion (THD)")
    add_paragraph("The audio engine executes FFT spectrum analysis over 16-bit PCM streaming buffers to inspect harmonic decay:")
    add_paragraph("THD Equation:  THD(%) = ( sqrt( V2^2 + V3^2 + V4^2 + ... ) / V1 ) * 100", indent=15, is_bold=True, color=(0.1, 0.2, 0.4))
    add_paragraph("- Codec Low-Pass Rolloff: Digital television, streaming video, and loudspeaker transducers exhibit steep low-pass filtering with cutoff frequencies fc <= 15.0 kHz.")
    add_paragraph("- Biological Human Distress Scream: Authentic human screams produce high-energy acoustic turbulence across the vocal folds with spectral bandwidth extending well beyond 16.0 kHz up to 20.0 kHz.")

    add_heading_2("D. Cross-Modal Confidence Score (CMCS)")
    add_paragraph("When telemetry inputs fall into intermediate or ambiguous zones, the engine applies weighted fusion:")
    add_paragraph("CMCS = (w_k * K_score) + (w_o * O_score) + (w_a * A_score)", indent=15, is_bold=True, color=(0.85, 0.12, 0.25))
    add_paragraph("Where:  w_k = 0.45 (Kinetic),  w_o = 0.25 (Optical),  w_a = 0.30 (Acoustic). Threshold CMCS >= 0.40 dispatches emergency; < 0.40 asserts veto.")

    add_heading_1("4. COMPREHENSIVE SENSOR TELEMETRY COLUMNS SCHEMA")
    add_paragraph("The system exposes deterministic column values across every evaluation snapshot, stored in Room SQLite:")

    headers = ["Domain", "Column Name", "Data Type", "Nominal Range", "Physical Significance"]
    col_widths = [70, 110, 65, 80, 170]
    rows = [
        ["Kinetic", "mean_g", "Float (g)", "0.8 - 3.5 g", "Average gravitational acceleration vector"],
        ["Kinetic", "jerk", "Float (g/s)", "0.0 - 8.0 g/s", "Rate of change of force; struggle intensity"],
        ["Kinetic", "rotational_var", "Float (rad/s^2)", "0.0 - 10.0", "Gyro variance; erratic fighting tumbling"],
        ["Kinetic", "motion_type", "String", "Enum", "STATIONARY, RHYTHMIC_WALKING, VIOLENT_STRUGGLE"],
        ["Optical", "prox_covered", "Boolean", "True / False", "Infrared proximity obstruction flag (pocket)"],
        ["Optical", "ambient_lux", "Float (lx)", "0.0 - 10k lx", "Ambient photometer (open air vs enclosed)"],
        ["Acoustic", "thd_percentage", "Float (%)", "1.0 - 50.0%", "Total Harmonic Distortion (speaker resonance)"],
        ["Acoustic", "high_freq_cutoff", "Float (kHz)", "8.0 - 20.0 kHz", "Spectral rolloff (TV codec vs bio scream)"],
        ["Acoustic", "voice_stress", "Boolean", "True / False", "Vocal fold distress classifier"],
        ["Engine", "action", "String", "VETO/DISPATCH", "Deterministic arbitration decision outcome"]
    ]
    add_table(headers, rows, col_widths)

    # ==================== PAGE 3 ====================
    start_new_page()
    add_heading_1("5. REAL-WORLD SCENARIOS & DECISION RULES")
    
    add_paragraph("Scenario A: Pocket / Bag Accidental Dial (ASSERT VETO)", is_bold=True, color=(0.18, 0.65, 0.42))
    add_paragraph("• Physical Trigger: Power button pressed 5 times while walking or phone bouncing inside handbag.")
    add_paragraph("• Telemetry State: Optical proximity covered (True), Ambient illuminance = 0.05 lx, Jerk = 0.65 g/s (< 2.0), Voice stress = False.")
    add_paragraph("• Decision Output: ACTION = 'VETO', Reason = 'ACCIDENTAL_POCKET_DIAL', Confidence = 97%, Haptic = 'SILENT_ABORT'.")
    add_paragraph("• Result: Baseband 1091 emergency call suppressed without disturbing police control desks.")
    current_y -= 6

    add_paragraph("Scenario B: Television / Entertainment Scream (ASSERT VETO)", is_bold=True, color=(0.85, 0.55, 0.05))
    add_paragraph("• Physical Trigger: Acoustic spike detector triggered by a movie, gaming stream, or video playback.")
    add_paragraph("• Telemetry State: Kinetic IMU indicates stationary rest on table (Jerk = 0.08 g/s, mean_g = 1.01), Audio spectral cutoff = 14.2 kHz (<= 15.0 kHz codec cutoff).")
    add_paragraph("• Decision Output: ACTION = 'VETO', Reason = 'MEDIA_PLAYBACK_DETECTED', Confidence = 93%, Haptic = 'SILENT_ABORT'.")
    add_paragraph("• Result: Loudspeaker scream rejected as artificial media; alarm silently aborted.")
    current_y -= 6

    add_paragraph("Scenario C1: Violent Physical Assault / Struggle (CONFIRM DISPATCH)", is_bold=True, color=(0.85, 0.12, 0.25))
    add_paragraph("• Physical Trigger: Power button pressed or shake triggered while victim is actively attacked.")
    add_paragraph("• Telemetry State: Kinetic Jerk = 4.82 g/s (> 3.0), Rotational variance = 6.15 rad/s^2, mean_g = 2.45 g.")
    add_paragraph("• Decision Output: ACTION = 'DISPATCH', Confidence = 96%, Countdown = 0s, Haptic = 'DISTRESS_CONFIRMED'.")
    add_paragraph("• Result: Zero-countdown emergency call to 1091, loud siren alarm, offline SMS dispatch, audio evidence recording.")
    current_y -= 6

    add_paragraph("Scenario C2: Authentic Human Distress Scream (CONFIRM DISPATCH)", is_bold=True, color=(0.85, 0.12, 0.25))
    add_paragraph("• Physical Trigger: Woman vocalizes high-amplitude scream for help.")
    add_paragraph("• Telemetry State: Voice stress = True, High frequency harmonic cutoff = 19.4 kHz (> 16.0 kHz uncompressed vocal folds).")
    add_paragraph("• Decision Output: ACTION = 'DISPATCH', Confidence = 95%, Countdown = 0s, Haptic = 'DISTRESS_CONFIRMED'.")
    add_paragraph("• Result: Immediate connection to Singappen Police Control Room.")

    add_heading_1("6. PATENT CLAIM DRAFTING BLUEPRINT (35 U.S.C. Section 101)")
    add_paragraph("To protect Sentinel AI's technical intellectual property, the method claims focus strictly on physical signal transformations rather than abstract machine learning:")
    add_paragraph("Independent System Claim 1: A mobile computing apparatus comprising: an inertial measurement unit, an optical sensor array, an acoustic transducer, a cellular baseband transceiver, and an execution processor coupled to memory, wherein the processor is configured to: detect an emergency trigger event while in a low-power dormant state; open a temporal micro-burst capture window of 2,000 milliseconds querying the inertial, optical, and acoustic transducers; compute an inertial jerk vector J = da/dt; compute an optical containment parameter; compute an acoustic frequency cutoff parameter; programmatically assert a veto command suppressing initiation of an outgoing baseband emergency dispatch to a remote police terminal upon determining the optical sensor is obstructed, the jerk vector remains below 2.0 g/s, and acoustic voice stress is absent; and selectively authorize baseband emergency dispatch upon detecting either jerk magnitude exceeding 3.0 g/s or an acoustic spectral rolloff exceeding 16.0 kHz.", indent=10, size=8.0, color=(0.15, 0.18, 0.24))

    if current_stream:
        pages_content.append("\n".join(current_stream))

    num_pages = len(pages_content)
    font1_idx = 3 + num_pages * 2
    font2_idx = font1_idx + 1

    page_obj_refs = [f"{3 + p * 2} 0 R" for p in range(num_pages)]
    pages_obj_body = f"<< /Type /Pages /Kids [{ ' '.join(page_obj_refs) }] /Count {num_pages} >>"
    catalog_obj_body = "<< /Type /Catalog /Pages 2 0 R >>"

    all_objs = [catalog_obj_body, pages_obj_body]

    for p_idx, content in enumerate(pages_content):
        stream_bytes = content.encode("latin-1", errors="replace")
        page_obj_num = 3 + p_idx * 2
        content_obj_num = page_obj_num + 1

        p_obj_body = f"<< /Type /Page /Parent 2 0 R /MediaBox [0 0 {PAGE_WIDTH} {PAGE_HEIGHT}] /Contents {content_obj_num} 0 R /Resources << /Font << /F1 {font1_idx} 0 R /F2 {font2_idx} 0 R >> >> >>"
        c_obj_body = f"<< /Length {len(stream_bytes)} >>\nstream\n{content}\nendstream"
        
        all_objs.append(p_obj_body)
        all_objs.append(c_obj_body)

    all_objs.append("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>")
    all_objs.append("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>")

    out = ["%PDF-1.4\n"]
    offsets = []
    for i, body in enumerate(all_objs):
        pos = sum(len(x.encode("latin-1", errors="replace")) for x in out)
        offsets.append(pos)
        out.append(f"{i+1} 0 obj\n{body}\nendobj\n")

    xref_pos = sum(len(x.encode("latin-1", errors="replace")) for x in out)
    total_objs = len(all_objs) + 1
    out.append(f"xref\n0 {total_objs}\n0000000000 65535 f \n")
    for off in offsets:
        out.append(f"{off:010d} 00000 n \n")
    out.append(f"trailer\n<< /Size {total_objs} /Root 1 0 R >>\nstartxref\n{xref_pos}\n%%EOF\n")

    dir_name = os.path.dirname(filepath)
    if dir_name:
        os.makedirs(dir_name, exist_ok=True)
    with open(filepath, "wb") as f:
        f.write("".join(out).encode("latin-1", errors="replace"))
    
    print(f"Generated PDF with {num_pages} pages at: {filepath}")

if __name__ == "__main__":
    target = sys.argv[1] if len(sys.argv) > 1 else "sentinel_ai_veto_engine_algorithms.pdf"
    build_pdf(target)
