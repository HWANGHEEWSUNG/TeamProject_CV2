from flask import Flask, request, render_template, jsonify
from transformers import pipeline

app = Flask(__name__)

# Hugging Face 감정 분석 파이프라인 초기화 (mBERT 사용)
# Use a pipeline as a high-level helper

emotion_pipeline = pipeline("text-classification", model="j-hartmann/emotion-english-distilroberta-base")
translation_pipeline = pipeline("translation", model="Helsinki-NLP/opus-mt-ko-en")

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/analyze_emotion', methods=['POST'])
def analyze_emotion():
    text = request.form['text']
    if not text:
        return jsonify({"error": "No text provided"}), 400

    # 텍스트를 영어로 번역
    translated_text = translation_pipeline(text)[0]['translation_text']

    # 번역된 텍스트로 감정 분석 수행
    results = emotion_pipeline(translated_text)
    return jsonify(results)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
