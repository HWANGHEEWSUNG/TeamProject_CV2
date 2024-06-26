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

def gen_frames():
    cap = cv2.VideoCapture(0)  # 웹캠으로부터 비디오 캡처 시작
    while True:
        success, frame = cap.read()
        if not success:
            break
        else:
            # 이미지 전처리
            n, c, h, w = input_blob.shape
            image = cv2.resize(frame, (w, h))
            image = image.transpose((2, 0, 1))  # HWC에서 CHW로 레이아웃 변경
            image = image.reshape((n, c, h, w))

            # 감정 인식 추론
            infer_request = exec_net.create_infer_request()
            infer_request.infer({input_blob.any_name: image})
            res = infer_request.get_output_tensor().data

            emotion = emotion_labels[np.argmax(res)]

            # 결과 표시
            cv2.putText(frame, emotion, (10, 50), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 0), 2, cv2.LINE_AA)
            ret, buffer = cv2.imencode('.jpg', frame)
            frame = buffer.tobytes()
            yield (b'--frame\r\n'
                   b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')  # 웹캠 프레임 반환

@app.route('/video_feed')
def video_feed():
    return Response(gen_frames(), mimetype='multipart/x-mixed-replace; boundary=frame')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
