import tensorflow as tf
import numpy as np
import os
import matplotlib.pyplot as plt
import skimage.io as io
import PIL as Image


from skimage import transform
np.expand_dims()
def encode():
    classes = ['0', '1','2','3','4','5','6','7','8']
    writer = tf.python_io.TFRecordWriter("training.tfrecords")

    for index, name in enumerate(classes):
        class_path = 'data/'+name+'/'
        print(index)
        print(name)
        for img_name in os.listdir(r'data/'+name):
            if img_name != '.DS_Store':
                img_path = class_path + img_name
                img = Image.open(img_path)
                img = img.resize((128, 128))
                img_raw = img.tobytes()
                example = tf.train.Example(features=tf.train.Features(feature={
                    "label": tf.train.Feature(int64_list=tf.train.Int64List(value=[index+1])),
                    'img_raw': tf.train.Feature(bytes_list=tf.train.BytesList(value=[img_raw]))
                }))
                writer.write(example.SerializeToString())

    writer.close()


def read_and_decode(tfrecords_file, batch_size):
    '''read and decode tfrecord file, generate (image, label) batches
    Args:
        tfrecords_file: the directory of tfrecord file
        batch_size: number of images in each batch
    Returns:
        image: 4D tensor - [batch_size, width, height, channel]
        label: 1D tensor - [batch_size]
    '''
    # make an input queue from the tfrecord file
    filename_queue = tf.train.string_input_producer([tfrecords_file])

    reader = tf.TFRecordReader()
    _, serialized_example = reader.read(filename_queue)
    img_features = tf.parse_single_example(
        serialized_example,
        features={
            'label': tf.FixedLenFeature([], tf.int64),
            'image_raw': tf.FixedLenFeature([], tf.string),
        })
    image = tf.decode_raw(img_features['image_raw'], tf.uint8)

    ##########################################################
    # you can put data augmentation here, I didn't use it
    ##########################################################
    # all the images of notMNIST are 28*28, you need to change the image size if you use other dataset.

    image = tf.reshape(image, [128, 128, 3])
    # image = tf.image.resize_image_with_crop_or_pad(image,280,280)
    label = tf.cast(img_features['label'], tf.int32)
    image_batch, label_batch = tf.train.batch([image, label],
                                              batch_size=batch_size,
                                              num_threads=64,
                                              capacity=2000)
    print(image_batch)
    print(tf.reshape(label_batch, [batch_size]))
    return image_batch, tf.reshape(label_batch, [batch_size])

def plot_images(images, labels):
    '''plot one batch size
    '''
    for i in np.arange(0, 10):
        plt.subplot(5, 5, i + 1)
        plt.axis('off')
        plt.title(chr(ord('A') + labels[i] - 1), fontsize = 14)
        plt.subplots_adjust(top=1.5)
        plt.imshow(images[i])
    plt.show()


def get_file(file_dir):
    images = []
    temp = []
    for root, sub_folders, files in os.walk(file_dir):
        # image directories
        for name in files:
            if name != '.DS_Store':
                images.append(os.path.join(root, name))
        # get 10 sub-folder names
        for name in sub_folders:
            temp.append(os.path.join(root, name))
    print(images)
    print(temp)
    # assign 10 labels based on the folder names
    labels = []
    for one_folder in temp:
        n_img = len(os.listdir(one_folder))-1
        letter = one_folder.split('/')[-1]
        if letter == '8':
            n_img = n_img + 1
        if letter == '0':
            labels = np.append(labels, n_img * [1])
        elif letter == '1':
            labels = np.append(labels, n_img * [2])
        elif letter == '2':
            labels = np.append(labels, n_img * [3])
        elif letter == '3':
            labels = np.append(labels, n_img * [4])
        elif letter == '4':
            labels = np.append(labels, n_img * [5])
        elif letter == '5':
            labels = np.append(labels, n_img * [6])
        elif letter == '6':
            labels = np.append(labels, n_img * [7])
        elif letter == '7':
            labels = np.append(labels, n_img * [8])
        elif letter == '8':
            labels = np.append(labels, n_img * [9])
    print(labels)
    print(len(labels))
    print(len(images))
    # shuffle
    temp1 = np.array([images, labels])
    temp1 = temp1.transpose()
    np.random.shuffle(temp1)

    print('temp1:')
    print(temp1[:,1])

    image_list = list(temp1[:,0])
    label_list = list(temp1[:,1])
    label_list = [int(float(i)) for i in label_list]

    return image_list, label_list


def convert_to_tfrecord(images, labels, save_dir, name):
    '''convert all images and labels to one tfrecord file.
    Args:
        images: list of image directories, string type
        labels: list of labels, int type
        save_dir: the directory to save tfrecord file, e.g.: '/home/folder1/'
        name: the name of tfrecord file, string type, e.g.: 'train'
    Return:
        no return
    Note:
        converting needs some time, be patient...
    '''

    filename = os.path.join(save_dir, name + '.tfrecords')
    n_samples = len(labels)

    if np.shape(images)[0] != n_samples:
        raise ValueError('Images size %d does not match label size %d.' % (images.shape[0], n_samples))

    # wait some time here, transforming need some time based on the size of your data.
    writer = tf.python_io.TFRecordWriter(filename)
    print('\nTransform start......')
    for i in np.arange(0, n_samples):
        try:
            image = io.imread(images[i])  # type(image) must be array!
            image_raw = image.tostring()
            label = int(labels[i])
            example = tf.train.Example(features=tf.train.Features(feature={
                'label': int64_feature(label),
                'image_raw': bytes_feature(image_raw)}))
            writer.write(example.SerializeToString())
        except IOError as e:
            print('Could not read:', images[i])
            print('error: %s' % e)
            print('Skip it!\n')
    writer.close()
    print('Transform done!')

def int64_feature(value):
  """Wrapper for inserting int64 features into Example proto."""
  if not isinstance(value, list):
    value = [value]
  return tf.train.Feature(int64_list=tf.train.Int64List(value=value))

def bytes_feature(value):
  return tf.train.Feature(bytes_list=tf.train.BytesList(value=[value]))

if __name__ == '__main__':
    #test_dir = './data'
    #save_dir = './data_tfrecords'
    #name_test = 'training'
    #images,labels = get_file(test_dir)
    #convert_to_tfrecord(images,labels,save_dir,name_test)
    images,labels = read_and_decode('./data_tfrecords/training.tfrecords',batch_size=100)
    print(images[0])
    plot_images(images[0], labels[0])