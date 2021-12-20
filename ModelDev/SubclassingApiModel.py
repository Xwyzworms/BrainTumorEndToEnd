## Gw pake subclassing yak, Biar paham aja arsitketurnya lebih dalam 
!gdown --id 1QQLpddvXqwSWt2WT-zmj_m2ty0ycKkru

import zipfile
with zipfile.ZipFile("data.zip") as fuf:
  fuf.extractall()

import tensorflow as tf
import numpy as np
import pandas as pd
import cv2
import os
import matplotlib.pyplot as plt
import seaborn as sns
import re as regex
from typing import Tuple, List
from sklearn.model_selection import train_test_split
from tensorflow.keras.layers import Dense, Input, Dropout, Conv2D, MaxPooling2D, BatchNormalization,Flatten
from sklearn.model_selection import train_test_split
from tensorflow.data import AUTOTUNE
from tensorflow.keras.utils import to_categorical
from tensorflow.keras.callbacks import EarlyStopping
from tensorflow.keras.applications.vgg16 import VGG16
tf.config.run_functions_eagerly(True)

#* Default Kernel
DEFAULT_KERNEL_SIZE : Tuple[int, int]= (3,3)
DEFAULT_POOL_SIZE : Tuple[int, int]= (3,3)

#* Default Filter Size  
DEFAULT_FILTER_SIZE : int = 32
DEFAULT_FILTER_SIZE2 : int = 64

#* Default Dense units
DEFAULT_UNITS1 : int = 128
DEFAULT_UNITS2 : int = 64
DEFAULT_UNITS3 : int = 512

#* Default Dropout
DROPOUT : int = 0.3

#HOW MANY
AUGMENT_TOTAL : int = 6

array = np.array
SIZE : int = (64,64)
DATA_PATH : str = "./data/brain_tumor/"
def preprocessImage(image : str, label : str) -> object:
	def _preprocessImage(image : str, label : str):
		image = image.numpy().decode()
		file_path = os.path.join(DATA_PATH, extract_theClass(image), image)
		image = tf.image.decode_jpeg(tf.io.read_file(file_path),channels=3)
		image = tf.image.resize(image, SIZE)
		image = image / 255
		image = tf.convert_to_tensor(image,dtype=tf.float32)
		#label = tf.convert_to_tensor(label,dtype=tf.int32)
		#image = tf.expand_dims(image, axis=0)
		#image.set_shape([ SIZE[0], SIZE[1], 1])
		return image, label
	return tf.py_function(_preprocessImage, inp=(image,label),Tout=(tf.float32, tf.float32))

def augment_data(image, label):
	def _augment(image, label):
		img_gen = tf.keras.preprocessing.image.ImageDataGenerator(rescale=1/255,brightness_range=[0.5,1.5])
		image = np.expand_dims(image,axis=0)
		img_gen.fit(image)
		img_results = [(image).astype(np.float32)]
		label_results = [label]
		augmented_images = [next(img_gen.flow(image)) for _ in range(AUGMENT_TOTAL)]
		labels = [label for _ in range(AUGMENT_TOTAL)]
		img_results.extend(augmented_images)
		label_results.extend(labels)
		img_results = np.squeeze(img_results)
		return img_results, label_results
	return tf.py_function(_augment,inp=(image,label),Tout=(tf.float32,tf.float32))
def extract_theClass(image : str) -> str:	
	result = regex.findall("[a-zA-Z]+",image)[0].lower()
	if result == "no" or result == "n":
		return "no"
	elif result == "y":
		return 'yes'
	return "random"

def create_dataFrame(path_data : str) -> pd.DataFrame:
	df = pd.DataFrame(columns=["image","label"])
	list_dir = os.listdir(path_data)
	for i in list_dir:
		full_path = os.path.join(path_data, i)
		for image in os.listdir(full_path):
			temp_df = pd.DataFrame(columns=["image","label"])
			temp_df = {"image" : image, "label" : i}
			df = df.append(temp_df, ignore_index=True)
	return df

def convert_toTfDataset(datas : array,y_true: array) -> Tuple[tf.data.Dataset, tf.data.Dataset]:
	x_train,x_val,y_train,y_val = train_test_split(datas,y_true,test_size=0.1,random_state=42,stratify=y_true)
	trainDataset = tf.data.Dataset.from_tensor_slices((tf.constant(x_train,dtype=tf.string),y_train))
	valDataset = tf.data.Dataset.from_tensor_slices((tf.constant(x_val,dtype=tf.string),y_val))
	return trainDataset, valDataset,y_val,x_val


def createModel():
	model_input = Input(shape=(64,64,3))
	layer = Conv2D(filters=DEFAULT_FILTER_SIZE, kernel_size=DEFAULT_KERNEL_SIZE, activation='relu',name="conv1")(model_input)
	layer = MaxPooling2D(pool_size=DEFAULT_POOL_SIZE)(layer)
	layer = BatchNormalization()(layer)
	layer = Conv2D(filters=DEFAULT_FILTER_SIZE2, kernel_size=DEFAULT_KERNEL_SIZE, activation='relu')(layer)
	layer = MaxPooling2D(pool_size=DEFAULT_POOL_SIZE)(layer)
	layer = BatchNormalization()(layer)
	layer = Flatten()(layer)
	layer = Dense(units=DEFAULT_UNITS1, activation='relu')(layer)
	layer = Dropout(DROPOUT)(layer)
	layer = Dense(units=DEFAULT_UNITS2, activation='relu')(layer)
	layer = Dense(units=DEFAULT_UNITS3, activation='relu')(layer)
	layer = Dense(units=3, activation='softmax')(layer)
	
	model = Model(inputs=model_input, outputs=layer)
	return model

class TumorModel(tf.keras.Model):
	def __init__(self, num_classes,):
		super(TumorModel, self).__init__()	
		self.vgg16 = VGG16(weights='imagenet', include_top=False,input_shape=(64,64,3))
		self.conv1 = Conv2D(filters=DEFAULT_FILTER_SIZE, kernel_size=DEFAULT_KERNEL_SIZE, activation='relu',name="conv1",padding="SAME")
		self.conv2 = Conv2D(filters=64, kernel_size=DEFAULT_KERNEL_SIZE, activation='relu',padding="SAME",name="conv2")
		self.conv3 = Conv2D(filters=DEFAULT_FILTER_SIZE2, kernel_size=DEFAULT_KERNEL_SIZE, activation='relu',padding="SAME",name="conv3")
		self.maxPooling1 = MaxPooling2D(pool_size=DEFAULT_POOL_SIZE)
		#self.batch = BatchNormalization()
		self.flatten = Flatten()
		self.dense1 = Dense(units=DEFAULT_UNITS1, activation='relu')
		self.dense2 = Dense(units=DEFAULT_UNITS2, activation='relu')
		self.dense3 = Dense(units=DEFAULT_UNITS3, activation='relu')
		#self.dropout= Dropout(DROPOUT)
		#self.dropout2 = Dropout(DROPOUT+0.3)
		self.outputGan = Dense(units=num_classes, activation='softmax')
		self.vgg16.trainable = False

	def call(self, input_tensors):
		layer_inputs = self.conv1(input_tensors)
		layer_inputs = self.maxPooling1(layer_inputs)
		layer_inputs = self.conv2(layer_inputs)
		layer_inputs = self.maxPooling1(layer_inputs)
		layer_inputs = self.conv3(layer_inputs)
		layer_inputs = self.maxPooling1(layer_inputs)
		h1 = self.flatten(layer_inputs);
		h1 = self.dense3(h1);
		h1 = self.dense1(h1);
		#h1 = self.dense1(h1)
		
		h1 = self.dense2(h1)
		h1 = self.outputGan(h1)
		return h1
	def model(self):
			x = tf.keras.Input(shape=(64,64,3))
			return tf.keras.Model(inputs=[x], outputs=self.call(x))
if __name__=="__main__":
		model = TumorModel(num_classes=3)
		model.compile(loss=tf.keras.losses.CategoricalCrossentropy(), optimizer=tf.keras.optimizers.Adam(0.001), metrics=['accuracy']);
		#model.build((16,64,64,3));
		#model.model().summary()
	
		df = create_dataFrame(path_data="./data/brain_tumor/")
		df["label"] = df["label"].replace({"no" : 0,"yes" : 1,"random":2})
		print(df["label"].value_counts())
		#df.to_csv("./data/brain_tumor/brain_tumor.csv")
		trainDataset, valDataset,y_val,x_val = convert_toTfDataset(df["image"].values, to_categorical(df["label"].values,num_classes=3))
		trainDataset = trainDataset.map(preprocessImage)
		valDataset = valDataset.map(preprocessImage)
		val_testing =valDataset
		trainDatset = trainDataset.map(augment_data).unbatch()
		valDataset = valDataset.map(augment_data).unbatch()
		trainDataset = trainDataset.batch(batch_size=16).prefetch(AUTOTUNE)
		valDataset = valDataset.batch(batch_size=16).prefetch(AUTOTUNE)
		model.fit(trainDataset, epochs=100, validation_data=valDataset,callbacks=[EarlyStopping(patience=5,monitor="val_loss")])

model.predict(valDataset)

from sklearn.metrics import classification_report
y_val_modified = np.argmax(y_val,axis=1)
y_val_modified
y_pred = model.predict(val_testing.batch(1))
print(classification_report(y_val_modified,np.argmax(y_pred,axis=1)))
#np.argmax(y_pred,axis=1)

model.save("model",save_format="tf")

converter = tf.lite.TFLiteConverter.from_saved_model("model")

tflite_model = converter.convert()

with open("model.tflite","wb") as fuf:
  fuf.write(tflite_model)

!tar -cvf model.tar.gz model