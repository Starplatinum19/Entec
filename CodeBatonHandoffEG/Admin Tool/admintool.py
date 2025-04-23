import os
import threading
import time
import json
from datetime import datetime
import tkinter as tk
from tkinter import ttk, messagebox
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import pandas as pd

def get_script_dir():
    """Returns the folder where this script lives."""
    return os.path.dirname(os.path.abspath(__file__))

class ScraperGUI:
    def __init__(self, root):
        self.root = root
        self.root.title("iPal Admin Tool - Scraper with Progress")
        self.root.geometry("550x250")

        self.label = tk.Label(root, text="Click to scrape and export JSON to Android assets", pady=10)
        self.label.pack()

        self.progress = ttk.Progressbar(root, length=400, mode='determinate')
        self.progress.pack(pady=10)

        self.percent_label = tk.Label(root, text="")
        self.percent_label.pack()

        self.status = tk.Label(root, text="Status: Idle", fg="blue")
        self.status.pack(pady=5)

        self.button = tk.Button(root, text="Run Scraper + Export", command=self.start_scraping)
        self.button.pack(pady=10)

    def update_status(self, text):
        self.status.config(text=f"Status: {text}")
        self.root.update_idletasks()

    def update_progress(self, current, total, start_time):
        percent = (current / total) * 100
        elapsed = time.time() - start_time
        eta = ((elapsed / current) * total - elapsed) if current else 0
        self.progress["value"] = percent
        self.percent_label.config(text=f"{percent:.1f}% complete | ETA: {int(eta)}s")
        self.root.update_idletasks()

    def start_scraping(self):
        threading.Thread(target=self.run_scraper_and_export).start()

    def run_scraper_and_export(self):
        try:
            self.update_status("Setting up browser...")
            service = Service(ChromeDriverManager().install())
            options = webdriver.ChromeOptions()
            options.add_argument("--headless")
            driver = webdriver.Chrome(service=service, options=options)

            self.update_status("Loading BLS page...")
            url = "https://data.bls.gov/projections/occupationProj"
            driver.get(url)
            time.sleep(5)

            self.update_status("Reading table rows...")
            table = driver.find_element(By.TAG_NAME, "table")
            rows = table.find_elements(By.TAG_NAME, "tr")

            self.update_status("Extracting headers...")
            headers = [th.text.strip() for th in rows[0].find_elements(By.TAG_NAME, "th")]
            headers = ['Occupation_ID' if 'Occupation Code' in h else h for h in headers]

            self.update_status("Parsing data...")
            data = []
            total_rows = len(rows) - 1
            start_time = time.time()

            for i, row in enumerate(rows[1:], start=1):
                cols = row.find_elements(By.TAG_NAME, "td")
                row_data = [col.text.strip() for col in cols]
                row_data += ["N/A"] * (len(headers) - len(row_data))
                data.append(row_data[:len(headers)])
                if i % 5 == 0 or i == total_rows:
                    self.update_progress(i, total_rows, start_time)

            driver.quit()

            self.update_status("Saving files...")
            df = pd.DataFrame(data, columns=headers)
            df.columns = [col.replace(" ", "_") for col in df.columns]
            json_data = df.to_dict(orient="records")

            today = datetime.now().strftime("%Y-%m-%d")
            json_filename = f"{today}_career_data.json"
            xlsx_filename = f"{today}_career_data.xlsx"

            # 1) Write JSON into Android assets folder
            script_dir = get_script_dir()
            assets_dir = os.path.normpath(
                os.path.join(script_dir, '..', 'Entec-main', 'app', 'src', 'main', 'assets')
            )
            os.makedirs(assets_dir, exist_ok=True)
            json_output_path = os.path.join(assets_dir, json_filename)
            with open(json_output_path, "w", encoding="utf-8") as f:
                json.dump(json_data, f, indent=2)

            # 2) Write Excel into Admin Tool/JobsData for Gradle to pick up
            jobs_data_dir = os.path.join(script_dir, 'JobsData')
            os.makedirs(jobs_data_dir, exist_ok=True)
            xlsx_output_path = os.path.join(jobs_data_dir, xlsx_filename)
            df.to_excel(xlsx_output_path, index=False)

            self.update_status("✅ Done!")
            messagebox.showinfo(
                "Success",
                f"Saved JSON to:\n{json_output_path}\n\n"
                f"Saved Excel to:\n{xlsx_output_path}"
            )

        except Exception as e:
            self.update_status("❌ Failed")
            messagebox.showerror("Error", str(e))

def run_gui():
    root = tk.Tk()
    ScraperGUI(root)
    root.mainloop()

if __name__ == "__main__":
    run_gui()
