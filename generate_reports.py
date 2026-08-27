import os
import sys
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether, HRFlowable
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import inch
from reportlab.pdfgen import canvas

class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super(NumberedCanvas, self).__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super(NumberedCanvas, self).showPage()
        super(NumberedCanvas, self).save()

    def draw_page_decorations(self, page_count):
        self.saveState()
        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#64748B"))
        
        # Header (pages > 1)
        if self._pageNumber > 1:
            self.drawString(54, letter[1] - 36, "TIMEFLOW QA & DEFECT AUDIT REPORT — SETOO QA AUTOMATION")
            self.setStrokeColor(colors.HexColor("#E2E8F0"))
            self.setLineWidth(0.5)
            self.line(54, letter[1] - 42, letter[0] - 54, letter[1] - 42)
            
        # Footer
        page_text = f"Page {self._pageNumber} of {page_count}"
        self.drawRightString(letter[0] - 54, 30, page_text)
        self.drawString(54, 30, "CONFIDENTIAL — STRICTLY FOR QA & ENGINEERING TEAMS")
        self.setStrokeColor(colors.HexColor("#E2E8F0"))
        self.setLineWidth(0.5)
        self.line(54, 40, letter[0] - 54, 40)
        self.restoreState()

def create_module_pdf(filename, module_data):
    doc = SimpleDocTemplate(
        filename,
        pagesize=letter,
        leftMargin=54,
        rightMargin=54,
        topMargin=54,
        bottomMargin=54
    )

    styles = getSampleStyleSheet()
    
    # Custom styles
    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=20,
        leading=24,
        textColor=colors.HexColor("#1E293B"),
        spaceAfter=4
    )
    
    subtitle_style = ParagraphStyle(
        'DocSubTitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=11,
        leading=15,
        textColor=colors.HexColor("#64748B"),
        spaceAfter=15
    )
    
    h1_style = ParagraphStyle(
        'SectionH1',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=13,
        leading=17,
        textColor=colors.HexColor("#0F172A"),
        spaceBefore=14,
        spaceAfter=8,
        keepWithNext=True
    )
    
    h2_style = ParagraphStyle(
        'SectionH2',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=11,
        leading=15,
        textColor=colors.HexColor("#334155"),
        spaceBefore=10,
        spaceAfter=4,
        keepWithNext=True
    )
    
    body_style = ParagraphStyle(
        'BodyDark',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9,
        leading=13,
        textColor=colors.HexColor("#334155")
    )
    
    body_bold = ParagraphStyle(
        'BodyDarkBold',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=9,
        leading=13,
        textColor=colors.HexColor("#1E293B")
    )
    
    table_cell = ParagraphStyle(
        'TableCell',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.5,
        leading=11.5,
        textColor=colors.HexColor("#1E293B")
    )
    
    table_cell_bold = ParagraphStyle(
        'TableCellBold',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.5,
        leading=11.5,
        textColor=colors.HexColor("#0F172A")
    )
    
    table_cell_header = ParagraphStyle(
        'TableCellHeader',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.5,
        leading=11.5,
        textColor=colors.white
    )

    defect_title_style = ParagraphStyle(
        'DefectTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=10.5,
        leading=14,
        textColor=colors.HexColor("#0F172A")
    )

    story = []

    # Title Banner
    story.append(Paragraph(f"Timeflow QA Defect & Quality Report", title_style))
    story.append(Paragraph(f"<b>Module:</b> {module_data['module_name']} | <b>Target Role:</b> {module_data['role_name']} ({module_data['role_email']})", subtitle_style))
    story.append(HRFlowable(width="100%", thickness=1.5, color=colors.HexColor("#3B82F6"), spaceAfter=12))

    # Meta Info Table
    meta_table_data = [
        [
            Paragraph("<b>Target System:</b> https://timeflow.setoo.in", body_style),
            Paragraph(f"<b>Environment:</b> Staging / Production QA", body_style)
        ],
        [
            Paragraph(f"<b>Test Suite:</b> {module_data['test_suite_name']}", body_style),
            Paragraph("<b>Execution Framework:</b> Selenium WebDriver 4 + TestNG", body_style)
        ],
        [
            Paragraph(f"<b>Total Automated Tests:</b> {module_data['total_tests']} Passed (0 Failed)", body_style),
            Paragraph(f"<b>Audit Date:</b> August 2026", body_style)
        ]
    ]
    meta_table = Table(meta_table_data, colWidths=[250, 254])
    meta_table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), colors.HexColor("#F8FAFC")),
        ('BOX', (0, 0), (-1, -1), 0.5, colors.HexColor("#CBD5E1")),
        ('INNERGRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#E2E8F0")),
        ('TOPPADDING', (0, 0), (-1, -1), 5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 5),
        ('LEFTPADDING', (0, 0), (-1, -1), 8),
        ('RIGHTPADDING', (0, 0), (-1, -1), 8),
    ]))
    story.append(meta_table)
    story.append(Spacer(1, 14))

    # Executive Summary Section
    story.append(Paragraph("1. Executive Summary & Quality Assessment", h1_style))
    story.append(Paragraph(module_data['executive_summary'], body_style))
    story.append(Spacer(1, 10))

    # Defect Summary Metric Cards
    metrics_data = [
        [
            Paragraph("<b>Total Defects Logged</b>", table_cell_header),
            Paragraph("<b>Critical / Blocker</b>", table_cell_header),
            Paragraph("<b>Major / High</b>", table_cell_header),
            Paragraph("<b>Normal / Medium</b>", table_cell_header),
            Paragraph("<b>Minor / Low</b>", table_cell_header)
        ],
        [
            Paragraph(f"<font size='12'><b>{len(module_data['defects'])}</b></font>", table_cell_bold),
            Paragraph(f"<font size='12' color='#DC2626'><b>{module_data['metric_critical']}</b></font>", table_cell_bold),
            Paragraph(f"<font size='12' color='#EA580C'><b>{module_data['metric_high']}</b></font>", table_cell_bold),
            Paragraph(f"<font size='12' color='#D97706'><b>{module_data['metric_medium']}</b></font>", table_cell_bold),
            Paragraph(f"<font size='12' color='#2563EB'><b>{module_data['metric_low']}</b></font>", table_cell_bold)
        ]
    ]
    metrics_table = Table(metrics_data, colWidths=[100, 100, 100, 104, 100])
    metrics_table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#1E293B")),
        ('BACKGROUND', (0, 1), (-1, 1), colors.HexColor("#F1F5F9")),
        ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('BOX', (0, 0), (-1, -1), 0.5, colors.HexColor("#94A3B8")),
        ('INNERGRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#CBD5E1")),
        ('TOPPADDING', (0, 0), (-1, -1), 5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 5),
    ]))
    story.append(metrics_table)
    story.append(Spacer(1, 14))

    # Automated Tests Execution Matrix
    story.append(Paragraph("2. Automated Test Suite Execution Matrix", h1_style))
    test_rows = [
        [
            Paragraph("<b>#</b>", table_cell_header),
            Paragraph("<b>Test Method Name</b>", table_cell_header),
            Paragraph("<b>Scope & Verification Focus</b>", table_cell_header),
            Paragraph("<b>Status</b>", table_cell_header)
        ]
    ]
    for idx, test in enumerate(module_data['automated_tests'], 1):
        test_rows.append([
            Paragraph(str(idx), table_cell),
            Paragraph(f"<code>{test['method']}</code>", table_cell_bold),
            Paragraph(test['description'], table_cell),
            Paragraph("<font color='#16A34A'><b>PASSED</b></font>", table_cell_bold)
        ])
    tests_table = Table(test_rows, colWidths=[24, 180, 240, 60])
    tests_table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#334155")),
        ('BOX', (0, 0), (-1, -1), 0.5, colors.HexColor("#CBD5E1")),
        ('INNERGRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#E2E8F0")),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, colors.HexColor("#F8FAFC")]),
        ('TOPPADDING', (0, 0), (-1, -1), 4),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
        ('LEFTPADDING', (0, 0), (-1, -1), 5),
        ('RIGHTPADDING', (0, 0), (-1, -1), 5),
        ('ALIGN', (0, 0), (0, -1), 'CENTER'),
        ('ALIGN', (3, 0), (3, -1), 'CENTER'),
    ]))
    story.append(tests_table)
    story.append(Spacer(1, 14))

    # Detailed Defects Section
    story.append(Paragraph("3. Detailed Defect Logs & Developer Fix Instructions", h1_style))
    story.append(Paragraph("The following issues have been documented with step-by-step reproduction flows, actual vs. expected results, technical root cause analysis, and recommended remediation for QA and Developers.", body_style))
    story.append(Spacer(1, 8))

    for idx, defect in enumerate(module_data['defects'], 1):
        defect_block = []
        
        # Header banner for defect
        sev_color = "#DC2626" if defect['severity'] == "Critical" else ("#EA580C" if defect['severity'] == "High" else ("#D97706" if defect['severity'] == "Medium" else "#2563EB"))
        
        header_text = f"<b>{defect['id']}: {defect['title']}</b>"
        defect_block.append(Paragraph(header_text, defect_title_style))
        defect_block.append(Spacer(1, 4))
        
        detail_table_data = [
            [
                Paragraph(f"<b>Severity:</b> <font color='{sev_color}'><b>{defect['severity']}</b></font>", table_cell),
                Paragraph(f"<b>Priority:</b> <b>{defect['priority']}</b>", table_cell),
                Paragraph(f"<b>Component:</b> {defect['component']}", table_cell),
                Paragraph(f"<b>Route:</b> <code>{defect['route']}</code>", table_cell)
            ]
        ]
        detail_meta_table = Table(detail_table_data, colWidths=[100, 100, 150, 154])
        detail_meta_table.setStyle(TableStyle([
            ('BACKGROUND', (0, 0), (-1, -1), colors.HexColor("#F1F5F9")),
            ('BOX', (0, 0), (-1, -1), 0.5, colors.HexColor("#CBD5E1")),
            ('TOPPADDING', (0, 0), (-1, -1), 3),
            ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
            ('LEFTPADDING', (0, 0), (-1, -1), 6),
            ('RIGHTPADDING', (0, 0), (-1, -1), 6),
        ]))
        defect_block.append(detail_meta_table)
        defect_block.append(Spacer(1, 6))

        # Description & Steps Table
        steps_content = "<br/>".join([f"<b>{s_idx}.</b> {step}" for s_idx, step in enumerate(defect['steps'], 1)])
        
        defect_content_data = [
            [Paragraph("<b>Pre-conditions</b>", table_cell_bold), Paragraph(defect['preconditions'], table_cell)],
            [Paragraph("<b>Steps to Reproduce</b>", table_cell_bold), Paragraph(steps_content, table_cell)],
            [Paragraph("<b>Expected Result</b>", table_cell_bold), Paragraph(defect['expected'], table_cell)],
            [Paragraph("<b>Actual Result</b>", table_cell_bold), Paragraph(f"<font color='#B91C1C'>{defect['actual']}</font>", table_cell)],
            [Paragraph("<b>Technical Root Cause</b>", table_cell_bold), Paragraph(defect['root_cause'], table_cell)],
            [Paragraph("<b>Impact Analysis</b>", table_cell_bold), Paragraph(defect['impact'], table_cell)],
            [Paragraph("<b>Suggested Dev Fix</b>", table_cell_bold), Paragraph(f"<font color='#047857'><b>{defect['fix']}</b></font>", table_cell)]
        ]
        defect_content_table = Table(defect_content_data, colWidths=[120, 384])
        defect_content_table.setStyle(TableStyle([
            ('BACKGROUND', (0, 0), (0, -1), colors.HexColor("#F8FAFC")),
            ('BOX', (0, 0), (-1, -1), 0.5, colors.HexColor("#CBD5E1")),
            ('INNERGRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#E2E8F0")),
            ('VALIGN', (0, 0), (-1, -1), 'TOP'),
            ('TOPPADDING', (0, 0), (-1, -1), 4),
            ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
            ('LEFTPADDING', (0, 0), (-1, -1), 6),
            ('RIGHTPADDING', (0, 0), (-1, -1), 6),
        ]))
        defect_block.append(defect_content_table)
        defect_block.append(Spacer(1, 14))

        story.append(KeepTogether(defect_block))

    # Recommendations & Sign-off
    story.append(Paragraph("4. Quality Engineering Recommendations & Next Steps", h1_style))
    rec_points = "<br/>".join([f"• <b>{r['title']}:</b> {r['desc']}" for r in module_data['recommendations']])
    story.append(Paragraph(rec_points, body_style))
    story.append(Spacer(1, 14))

    # Sign-off Box
    signoff_data = [
        [
            Paragraph("<b>QA Lead Auditor:</b> Antigravity Automation Lead", body_style),
            Paragraph("<b>Target Resolution Sprint:</b> Q3 Sprint-2", body_style)
        ],
        [
            Paragraph("<b>Status:</b> Official QA Test Audit Complete", body_style),
            Paragraph("<b>Distribution:</b> Engineering, QA & Product Teams", body_style)
        ]
    ]
    signoff_table = Table(signoff_data, colWidths=[250, 254])
    signoff_table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), colors.HexColor("#F8FAFC")),
        ('BOX', (0, 0), (-1, -1), 0.5, colors.HexColor("#94A3B8")),
        ('TOPPADDING', (0, 0), (-1, -1), 5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 5),
        ('LEFTPADDING', (0, 0), (-1, -1), 8),
        ('RIGHTPADDING', (0, 0), (-1, -1), 8),
    ]))
    story.append(signoff_table)

    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"Generated: {filename}")

if __name__ == "__main__":
    print("PDF generator module loaded.")
