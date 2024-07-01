from flask import Flask, Response, request, render_template, jsonify
from video_feed import EmotionRecognition
from analyze_emotion import TextEmotionAnalysis

app = Flask(__name__)
app.config['JSON_AS_ASCII'] = False

video_emotion_recognition = EmotionRecognition()
text_emotion_analysis = TextEmotionAnalysis()


@app.route('/')
def index():
    return render_template('index.html')


@app.route('/video_feed')
def video_feed():
    return Response(video_emotion_recognition.gen_frames(), mimetype='multipart/x-mixed-replace; boundary=frame')


@app.route('/analyze_emotion', methods=['POST'])
def analyze_emotion():
    text = request.data.decode('utf-8')
    if not text:
        return jsonify({"error": "No text provided"}), 400

    print("Received text:", text)  # 디버깅을 위해 추가

    results = text_emotion_analysis.analyze(text)
    print("Results:", results)  # 디버깅을 위해 추가
    return jsonify(results)


@app.route('/get_emotion')
def get_emotion():
    return video_emotion_recognition.get_emotion()


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)
