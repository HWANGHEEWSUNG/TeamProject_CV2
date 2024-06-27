from flask import Flask, Response
import cv2
import numpy as np
from openvino.runtime import Core

app = Flask(__name__)

# OpenVINO Inference Engine 초기화
ie = Core()
model_xml = "./models/FP32/emotions-recognition-retail-0003.xml"
model_bin = "./models/FP32/emotions-recognition-retail-0003.bin"
net = ie.read_model(model=model_xml, weights=model_bin)
exec_net = ie.compile_model(model=net, device_name="CPU")

input_blob = next(iter(net.inputs))
output_blob = next(iter(net.outputs))

# 감정 인식 라벨
emotion_labels = ["neutral", "happy", "sad", "surprise", "anger"]

# OpenCV의 얼굴 탐지기 초기화 (Haar Cascade 사용)
face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')

def gen_frames():
    cap = cv2.VideoCapture(0)  # 웹캠으로부터 비디오 캡처 시작
    while True:
        success, frame = cap.read()
        if not success:
            break
        else:
            # 얼굴 탐지
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            faces = face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5, minSize=(30, 30))

            # 가장 큰 얼굴 찾기
            if len(faces) > 0:
                largest_face = max(faces, key=lambda rect: rect[2] * rect[3])  # 얼굴 크기에 따라 선택
                (x, y, w, h) = largest_face

                # 얼굴 영역 추출 및 전처리
                face_img = frame[y:y+h, x:x+w]
                n, c, h, w = input_blob.shape
                face_img = cv2.resize(face_img, (w, h))
                face_img = face_img.transpose((2, 0, 1))  # HWC에서 CHW로 레이아웃 변경
                face_img = face_img.reshape((n, c, h, w))

                # 감정 인식 추론
                infer_request = exec_net.create_infer_request()
                infer_request.infer({input_blob.any_name: face_img})
                res = infer_request.get_output_tensor().data

                emotion = emotion_labels[np.argmax(res)]

                # 결과 표시
                # cv2.rectangle(frame, (x, y), (x + w, y + h), (255, 0, 0), 2)
                cv2.putText(frame, emotion, (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0, 255, 0), 2)

            ret, buffer = cv2.imencode('.jpg', frame)
            frame = buffer.tobytes()
            yield (b'--frame\r\n'
                   b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')  # 웹캠 프레임 반환

@app.route('/video_feed')
def video_feed():
    return Response(gen_frames(), mimetype='multipart/x-mixed-replace; boundary=frame')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
