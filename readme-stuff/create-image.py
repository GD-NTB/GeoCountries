import subprocess, re, os
from PIL import Image, ImageDraw, ImageFont

ROOT_FOLDER = 'D:/GeoCountries/Plugin/GeoCountries/src'
SAVE_TO_FILE = 'image.png'
LANGUAGE = 'java'

tokei_output = subprocess.run(
    ["tokei", ROOT_FOLDER],
    capture_output=True,
    text=True
).stdout

loc = 0
for line in tokei_output.split('\n'):
    language = line[1:5].lower()
    if language != LANGUAGE:
        continue
    line_split = re.sub(r'\s+', '_', line[1:]).split('_')
    loc = int(line_split[3])

# create image
def create_image(size, bg_colour, message, font_colour, font_size):
    image = Image.new('RGB', size, bg_colour)
    font = ImageFont.truetype(r'C:\Windows\Fonts\comic.ttf', font_size)
    draw = ImageDraw.Draw(image)
    text_x = (image.width) // 2
    text_y = (image.height) // 2
    draw.text((text_x, text_y), message, font=font, fill=font_colour, anchor="mm")
    return image

text = 'lines of code:\n\n' + str(loc)
font_size = 25
image = create_image((200, 200), 'black', text, 'white', font_size)
# save image
dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), SAVE_TO_FILE)
image.save(dir)

