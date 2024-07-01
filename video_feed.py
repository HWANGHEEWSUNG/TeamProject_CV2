import cv2
from flask import jsonify
from deepface import DeepFace

class EmotionRecognition:
    def __init__(self):
        # 비디오 캡처 초기화
        self.cap = cv2.VideoCapture(0)
        # Haar Cascade 초기화
        self.face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')

    def gen_frames(self):
        while True:
            success, frame = self.cap.read()
            if not success:
                break
            else:
                # 얼굴 탐지
                gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
                faces = self.face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5, minSize=(30, 30))

                for (x, y, w, h) in faces:
                    # 얼굴 영역 추출
                    face_img = frame[y:y+h, x:x+w]

                    # DeepFace를 사용하여 감정 인식 수행
                    try:
                        objs = DeepFace.analyze(face_img, actions=["emotion"])
                        dominant_emotion = objs[0]["dominant_emotion"]
                        # 감정 텍스트를 프레임에 표시
                        cv2.putText(frame, dominant_emotion, (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0, 255, 0), 2, cv2.LINE_AA)
                    except Exception as e:
                        print(f"Error analyzing face: {e}")

                    # 얼굴 영역에 사각형 그리기
                    cv2.rectangle(frame, (x, y), (x+w, y+h), (255, 0, 0), 2)

                ret, buffer = cv2.imencode('.jpg', frame)
                frame = buffer.tobytes()
                yield (b'--frame\r\n'
                       b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')

    def get_emotion(self):
        success, frame = self.cap.read()
        if success:
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            faces = self.face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5, minSize=(30, 30))
            if len(faces) > 0:
                (x, y, w, h) = faces[0]
                face_img = frame[y:y+h, x:x+w]
                try:
                    objs = DeepFace.analyze(face_img, actions=["emotion"])
                    return jsonify(objs[0])
                except Exception as e:
                    return jsonify({"error": str(e)})
            return jsonify({"error": "No face detected"})
        return jsonify({"error": "No frame captured"})
