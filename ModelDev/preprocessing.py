#%%
import numpy as np
import pandas as pd
import cv2
import os
import matplotlib.pyplot as plt
import seaborn as sns
import re as regex
import tensorflow as tf
from typing import Tuple, List
from sklearn.model_selection import train_test_split
from tensorflow.data import AUTOTUNE
array = np.array
SIZE : int = (224,224)
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
	x_train,x_val,y_train,y_val = train_test_split(datas,y_true,test_size=0.2,random_state=42)
	trainDataset = tf.data.Dataset.from_tensor_slices((tf.constant(x_train,dtype=tf.string),y_train))
	valDataset = tf.data.Dataset.from_tensor_slices((tf.constant(x_val,dtype=tf.string),y_val))
	return trainDataset, valDataset

