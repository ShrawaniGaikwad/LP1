import tkinter as tk
from tkinter import messagebox

class App:
    def __init__(self, root):
        self.root = root
        self.root.title("VPTronics Home Page")
        self.root.geometry("600x600")
        self.root.configure(bg="#f0f0f0")

        # Create a navigation frame (Header)
        self.nav_frame = tk.Frame(root, bg="#2c3e50")
        self.nav_frame.pack(fill=tk.X)

        # Navigation buttons
        self.home_button = tk.Button(self.nav_frame, text="Home", command=self.show_home, bg="#3498db", fg="white")
        self.home_button.pack(side=tk.LEFT, padx=5, pady=5)

        self.products_button = tk.Button(self.nav_frame, text="Products", command=self.show_products, bg="#3498db", fg="white")
        self.products_button.pack(side=tk.LEFT, padx=5, pady=5)

        self.about_button = tk.Button(self.nav_frame, text="About", command=self.show_about, bg="#3498db", fg="white")
        self.about_button.pack(side=tk.LEFT, padx=5, pady=5)

        self.contact_button = tk.Button(self.nav_frame, text="Contact", command=self.show_contact, bg="#3498db", fg="white")
        self.contact_button.pack(side=tk.LEFT, padx=5, pady=5)

        # Create frames for different pages
        self.home_frame = tk.Frame(root, bg="#f0f0f0")
        self.products_frame = tk.Frame(root, bg="#f0f0f0")
        self.about_frame = tk.Frame(root, bg="#f0f0f0")
        self.contact_frame = tk.Frame(root, bg="#f0f0f0")

        # Set up Home frame
        self.home_label = tk.Label(self.home_frame, text="Welcome to VPTronics", bg="#f0f0f0", font=("Arial", 16))
        self.home_label.pack(pady=20)
        
        self.welcome_text = tk.Label(self.home_frame, text="A well-trusted company with a good service sector", bg="#f0f0f0", font=("Arial", 12))
        self.welcome_text.pack(pady=10)

        self.carousel_frame = tk.Frame(self.home_frame, bg="#dfe6e9")
        self.carousel_frame.pack(pady=10, padx=10)

        # Placeholder for carousel images
        self.carousel_label = tk.Label(self.carousel_frame, text="Carousel Image 1", bg="#bdc3c7", width=40, height=5)
        self.carousel_label.pack()
        self.products_grid = tk.Frame(self.home_frame, bg="#f0f0f0")
        self.products_grid.pack()

        # Adding product entries
        self.add_product(self.products_grid, "Img1", "Product 1 Description")
        self.add_product(self.products_grid, "Img2", "Product 2 Description")
        self.add_product(self.products_grid, "Img3", "Product 3 Description")
        self.add_product(self.products_grid, "Img4", "Product 4 Description")

        # Vision and Mission Section
        self.vision_label = tk.Label(self.home_frame, text="Our Vision", bg="#f0f0f0", font=("Arial", 14, "bold"))
        self.vision_label.pack(pady=10)
        self.vision_text = tk.Label(self.home_frame, text="To be the leading provider of innovative electronics, enhancing lives through cutting-edge technology.", bg="#f0f0f0", font=("Arial", 12), wraplength=500)
        self.vision_text.pack(pady=5)

        self.mission_label = tk.Label(self.home_frame, text="Our Mission", bg="#f0f0f0", font=("Arial", 14, "bold"))
        self.mission_label.pack(pady=10)
        self.mission_text = tk.Label(self.home_frame, text="We are committed to delivering high-quality electronics and services that exceed our customers' expectations and contribute to a sustainable future.", bg="#f0f0f0", font=("Arial", 12), wraplength=500)
        self.mission_text.pack(pady=5)

        # Set up Products frame
        self.products_label = tk.Label(self.products_frame, text="Products", bg="#f0f0f0", font=("Arial", 16))
        self.products_label.pack(pady=10)

        self.products_grid = tk.Frame(self.products_frame, bg="#f0f0f0")
        self.products_grid.pack()

        # Adding product entries
        self.add_product(self.products_grid, "Img1", "Product 1 Description")
        self.add_product(self.products_grid, "Img2", "Product 2 Description")
        self.add_product(self.products_grid, "Img3", "Product 3 Description")
        self.add_product(self.products_grid, "Img4", "Product 4 Description")

        # Set up About frame with more content
        self.about_label = tk.Label(self.about_frame, text="About Us", bg="#f0f0f0", font=("Arial", 16))
        self.about_label.pack(pady=20)

        self.about_text = tk.Label(self.about_frame, text="We are a leading electronics company", bg="#f0f0f0", font=("Arial", 12))
        self.about_text.pack(pady=10)

        self.our_team_label = tk.Label(self.about_frame, text="Our Team", bg="#f0f0f0", font=("Arial", 14, "bold"))
        self.our_team_label.pack(pady=10)
        self.our_team_text = tk.Label(self.about_frame, text="We have a team of highly skilled professionals dedicated to innovation and customer satisfaction.", bg="#f0f0f0", font=("Arial", 12), wraplength=500)
        self.our_team_text.pack(pady=5)

        self.our_history_label = tk.Label(self.about_frame, text="Our History", bg="#f0f0f0", font=("Arial", 14, "bold"))
        self.our_history_label.pack(pady=10)
        self.our_history_text = tk.Label(self.about_frame, text="Since our inception in 2000, VPTronics has grown from a small startup to an international leader in electronics.", bg="#f0f0f0", font=("Arial", 12), wraplength=500)
        self.our_history_text.pack(pady=5)

        self.our_values_label = tk.Label(self.about_frame, text="Our Values", bg="#f0f0f0", font=("Arial", 14, "bold"))
        self.our_values_label.pack(pady=10)
        self.our_values_text = tk.Label(self.about_frame, text="Innovation, Integrity, and Customer Commitment are at the core of everything we do.", bg="#f0f0f0", font=("Arial", 12), wraplength=500)
        self.our_values_text.pack(pady=5)

        # Set up Contact frame with more styling and input fields
        self.contact_label = tk.Label(self.contact_frame, text="Contact Us", bg="#f0f0f0", font=("Arial", 16))
        self.contact_label.pack(pady=20)

        self.name_label = tk.Label(self.contact_frame, text="Name:", bg="#f0f0f0", font=("Arial", 12))
        self.name_label.pack(pady=5)
        self.name_entry = tk.Entry(self.contact_frame, width=30)
        self.name_entry.pack(pady=5)

        self.email_label = tk.Label(self.contact_frame, text="Email:", bg="#f0f0f0", font=("Arial", 12))
        self.email_label.pack(pady=5)
        self.email_entry = tk.Entry(self.contact_frame, width=30)
        self.email_entry.pack(pady=5)

        self.phone_label = tk.Label(self.contact_frame, text="Phone:", bg="#f0f0f0", font=("Arial", 12))
        self.phone_label.pack(pady=5)
        self.phone_entry = tk.Entry(self.contact_frame, width=30)
        self.phone_entry.pack(pady=5)

        self.message_label = tk.Label(self.contact_frame, text="Message:", bg="#f0f0f0", font=("Arial", 12))
        self.message_label.pack(pady=5)
        self.message_entry = tk.Text(self.contact_frame, height=5, width=30)
        self.message_entry.pack(pady=5)

        self.submit_button = tk.Button(self.contact_frame, text="Submit", command=self.submit_message, bg="#3498db", fg="white")
        self.submit_button.pack(pady=10)

        # Start with the home page
        self.show_home()

    def add_product(self, frame, img_text, description):
        product_frame = tk.Frame(frame, bg="#dfe6e9", padx=10, pady=10)
        product_frame.pack(side=tk.LEFT, padx=10, pady=10)

        img_label = tk.Label(product_frame, text=img_text, bg="#bdc3c7", width=20, height=10)
        img_label.pack()

        desc_label = tk.Label(product_frame, text=description, bg="#f0f0f0", font=("Arial", 10))
        desc_label.pack(pady=5)

        details_button = tk.Button(product_frame, text="View Details", bg="#3498db", fg="white")
        details_button.pack(pady=5)

    def show_home(self):
        self.hide_all_frames()
        self.home_frame.pack(fill="both", expand=1)

    def show_products(self):
        self.hide_all_frames()
        self.products_frame.pack(fill="both", expand=1)

    def show_about(self):
        self.hide_all_frames()
        self.about_frame.pack(fill="both", expand=1)

    def show_contact(self):
        self.hide_all_frames()
        self.contact_frame.pack(fill="both", expand=1)

    def hide_all_frames(self):
        self.home_frame.pack_forget()
        self.products_frame.pack_forget()
        self.about_frame.pack_forget()
        self.contact_frame.pack_forget()

    def submit_message(self):
        name = self.name_entry.get()
        email = self.email_entry.get()
        phone = self.phone_entry.get()
        message = self.message_entry.get("1.0", "end-1c")

        if not name or not email or not message:
            messagebox.showwarning("Input Error", "Please fill out all required fields.")
        else:
            messagebox.showinfo("Thank You", f"Thank you, {name}! We will get back to you soon.")
            self.clear_contact_form()

    def clear_contact_form(self):
        self.name_entry.delete(0, tk.END)
        self.email_entry.delete(0, tk.END)
        self.phone_entry.delete(0, tk.END)
        self.message_entry.delete("1.0", tk.END)

root = tk.Tk()
app = App(root)
root.mainloop()
