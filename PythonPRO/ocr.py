from flask import Flask, request, jsonify
from PIL import Image
import pytesseract
import io

# Set the path to the tesseract executable
pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'

app = Flask(__name__)


@app.route('/ocr', methods=['POST'])
def perform_ocr():
    try:
        if 'file' not in request.files:
            return jsonify({'error': 'No image provided'}), 400
        
        image_file = request.files['file']
        image = Image.open(io.BytesIO(image_file.read()))
        extracted_text = pytesseract.image_to_string(image)
        
        return jsonify({'text': extracted_text}), 200
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001)
