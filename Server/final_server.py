from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
from datetime import datetime
import math
import time
import numpy as np
import tensorflow as tf
import model
import logging
from flask import Flask, jsonify, request, abort, make_response
import os,base64,time
import os
import skimage.io as io

logging.basicConfig(level=logging.INFO)
app = Flask(__name__)
PORT = 8000

#%%
FLAGS = tf.app.flags.FLAGS

tf.app.flags.DEFINE_string('eval_dir', './Group/cnn_train/testing_log',
                           """Directory where to write event logs.""")
tf.app.flags.DEFINE_string('eval_data', 'test',
                           """Either 'test' or 'train_eval'.""")
tf.app.flags.DEFINE_string('checkpoint_dir', './Group/cnn_train/training_log',
                           """Directory where to read model checkpoints.""")
tf.app.flags.DEFINE_integer('eval_interval_secs', 60 * 1,
                            """How often to run the eval.""")
tf.app.flags.DEFINE_integer('num_examples', 813,
                            """Number of examples to run.""")
tf.app.flags.DEFINE_boolean('run_once', True,
                         """Whether to run eval only once.""")

#%%
def eval_once(saver, summary_writer, top_k_op, summary_op):

  with tf.Session() as sess:
    ckpt = tf.train.get_checkpoint_state(FLAGS.checkpoint_dir)
    if ckpt and ckpt.model_checkpoint_path:
      saver.restore(sess, ckpt.model_checkpoint_path)
      global_step = ckpt.model_checkpoint_path.split('/')[-1].split('-')[-1]
    else:
      print('No checkpoint file found')
      return
    # Start the queue runners.
    coord = tf.train.Coordinator()
    try:
      threads = []
      for qr in tf.get_collection(tf.GraphKeys.QUEUE_RUNNERS):
        threads.extend(qr.create_threads(sess, coord=coord, daemon=True,
                                         start=True))
      num_iter = int(math.ceil(FLAGS.num_examples / FLAGS.batch_size))
      true_count = 0  # Counts the number of correct predictions.
      total_sample_count = num_iter * FLAGS.batch_size
      step = 0
      predictions = sess.run([top_k_op])
      true_count += np.sum(predictions)
      step += 1
      print(predictions)
      # Compute precision @ 1.
      precision = true_count / FLAGS.batch_size
      return precision
      #print(predictions)
      #return precision
      print('%s: prediction @ 1 = %.3f' % (datetime.now(), precision))

      summary = tf.Summary()
      summary.ParseFromString(sess.run(summary_op))
      summary.value.add(tag='Precision @ 1', simple_value=precision)
      summary_writer.add_summary(summary, global_step)
    except Exception as e:  # pylint: disable=broad-except
      coord.request_stop(e)

    coord.request_stop()
    coord.join(threads, stop_grace_period_secs=10)

#%%
def evaluate():
  with tf.Graph().as_default() as g:
    eval_data = 'test'
    images, labels = model.inputs(is_train=eval_data)
    logits = model.inference(images)
    top_k_op = tf.nn.in_top_k(logits, labels, 1)
    variable_averages = tf.train.ExponentialMovingAverage(
        model.MOVING_AVERAGE_DECAY)
    variables_to_restore = variable_averages.variables_to_restore()
    saver = tf.train.Saver(variables_to_restore)
    summary_op = tf.summary.merge_all()
    summary_writer = tf.summary.FileWriter(FLAGS.eval_dir, g)
    while True:
      return eval_once(saver, summary_writer, top_k_op, summary_op)
      if FLAGS.run_once:
        break
    
def get_file(file_dir,letter):
    images = []
    temp = []
    for root, sub_folders, files in os.walk(file_dir):
        for name in files:
            images.append(os.path.join(root, name))
        for name in sub_folders:
            temp.append(os.path.join(root, name))
    labels = []        
    for one_folder in temp:        
        n_img = len(os.listdir(one_folder))
        #letter = one_folder.split('/')[-1]
        if letter=='1':
            labels = np.append(labels, n_img*[1])
        elif letter=='2':
            labels = np.append(labels, n_img*[2])
        elif letter=='3':
            labels = np.append(labels, n_img*[3])
        elif letter=='4':
            labels = np.append(labels, n_img*[4])
        elif letter=='5':
            labels = np.append(labels, n_img*[5])
        elif letter=='6':
            labels = np.append(labels, n_img*[6])
        elif letter=='7':
            labels = np.append(labels, n_img*[7])
        elif letter=='8':
            labels = np.append(labels, n_img*[8])
        elif letter=='9':
            labels = np.append(labels, n_img*[9])
        else:
            labels = np.append(labels, n_img*[0])
    temp = np.array([images, labels])
    temp = temp.transpose()
    #np.random.shuffle(temp)
    print(labels)
    image_list = list(temp[:, 0])
    label_list = list(temp[:, 1])
    label_list = [int(float(i)) for i in label_list]
    return image_list, label_list
    
def convert_to_tfrecord(images, labels, save_dir, name):
    filename = os.path.join(save_dir, name + '.tfrecords')
    n_samples = len(labels)
    if np.shape(images)[0] != n_samples:
        raise ValueError('Images size %d does not match label size %d.' %(images.shape[0], n_samples))
    # wait some time here, transforming need some time based on the size of your data.
    writer = tf.python_io.TFRecordWriter(filename)
    print('\nTransform start......')
    for i in np.arange(0, n_samples):
        try:
            image = io.imread(images[i]) # type(image) must be array!
            image_raw = image.tostring()
            label = int(labels[i])
            example = tf.train.Example(features=tf.train.Features(feature={
                            'label':int64_feature(label),
                            'image_raw': bytes_feature(image_raw)}))
            writer.write(example.SerializeToString())
        except IOError as e:
            print('Could not read:', images[i])
            print('error: %s' %e)
            print('Skip it!\n')
    writer.close()
    print('Transform done!')
    
def int64_feature(value):
  if not isinstance(value, list):
    value = [value]
  return tf.train.Feature(int64_list=tf.train.Int64List(value=value))

def bytes_feature(value):
  return tf.train.Feature(bytes_list=tf.train.BytesList(value=[value]))
  
def convert(letter):
    test_dir = './Group/cnn_train/imgDetect'
    save_dir = './Group/cnn_train/tfrecords_data'
    name_test = 'testing_once'
    images, labels = get_file(test_dir,letter)
    convert_to_tfrecord(images, labels, save_dir, name_test)

def _parse_request_body():
    """Extract first and last name from request."""
    request_body = request.get_json()
    try:
        img = request_body['img']
    except KeyError:
        # Missing first or last name fields.
        logging.info('Badly formatted request body: {}'.format(request_body))
        response_body = {
            'error': "Missing 'firstName' or 'lastName' key in request body"
        }
        response = make_response(jsonify(response_body), 400)
        abort(response)
    return img

def _save_img(img_base64,filedir):
    imgData = base64.b64decode(img_base64)
    filename = filedir+'/userPhoto'+'.jpg'
    file = open(filename,'wb')
    file.write(imgData)
    file.close()
    
def convertPredict(predict):
    if predict == 1:
        return 'Eiffel Tower'
    elif predict == 2:
        return 'Big Ben'
    elif predict == 3:
        return 'London Bridge'
    elif predict == 4:
        return 'London Eye'
    elif predict == 5:
        return 'Sydney Opera House'
    elif predict == 6:
        return 'Pyramid'
    elif predict == 7:
        return 'The Palace Museum'
    elif predict == 8:
        return 'The State Of Liberty'
    elif predict == 9:
        return 'Tian An Men Square'
    else:
        return 'Can not detect this image!'

@app.route('/predict', methods=['POST'])
def predict():
    """Respond to requests for a prediction."""
    img = _parse_request_body()
    _save_img(img,'./Group/cnn_train/imgDetect/0')
    predict = 0
    for i in range(9):
        temp = i + 1
        convert(str(temp))
        result = evaluate()
        if result == 1:
            predict = temp
            break
        else:
            continue
    predict = convertPredict(predict)
    print(predict)
    json_result = jsonify({
        "code": "200",
        "msg":{
            "result":predict
        }
    })
    return json_result

def test():
    with open('./Group/cnn_train/4.jpg', 'rb') as f:
        data = f.read()
        encodestr = base64.b64encode(data)
        encodestr = str(encodestr,'utf-8')
        #print(encodestr)
        predict(encodestr)

if __name__ == '__main__':
  #tf.app.run()
  print('Server Start.....')
  logging.info('Listening on port {}'.format(PORT))
  app.run(debug=True, host='127.0.0.1', port=PORT)
  #print(predict())
  #test()
 
  
