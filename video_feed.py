import cv2
from flask import jsonify
from deepface import DeepFace

class EmotionRecognition:
    def __init__(self):
        # 비디오 캡처 초기화
        self.cap = cv2.VideoCapture(0)
        self.fps = 5  # 노트북 웹캠 플리커링으로 표정 인식 낮아짐; 프레임 낮춤으로 인식률 증가
        # Haar Cascade 초기화
        # 확인 결과, DeepFace.analyze 기본 값이 opencv며 얼굴 인식이 haar cascade를 사용; haar cascade 삭제

    def gen_frames(self):
        while True:
            success, frame = self.cap.read()
            if not success:
                break
            else:
                # DeepFace를 사용하여 감정 인식 수행
                try:
                    objs = DeepFace.analyze(frame, actions=["emotion"], detector_backend="fastmtcnn")  # 기본값인 opencv가 얼굴 인식이 낮은 편이라 fastcnn으로 변경
                    dominant_emotion = objs[0]["dominant_emotion"]
                    # 감정 텍스트를 프레임에 표시
                    cv2.putText(frame, dominant_emotion, (50, 50), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 0), 2,
                                cv2.LINE_AA)
                except Exception as e:
                    print(f"Error analyzing frame: {e}")

                ret, buffer = cv2.imencode('.jpg', frame)
                frame = buffer.tobytes()
                yield (b'--frame\r\n'
                       b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')

    def get_emotion(self):
        success, frame = self.cap.read()
        if success:
            try:
                objs = DeepFace.analyze(frame, actions=["emotion"], detector_backend="fastmtcnn")
                return jsonify(objs[0])
            except Exception as e:
                return jsonify({"error": str(e)})
        return jsonify({"error": "No frame captured"})
