#%%
from preprocessing import *
import tensorflow as tf
from tensorflow.keras.models import Model
from tensorflow.keras.layers import Dense, Input, Dropout, Conv2D, MaxPooling2D, BatchNormalization,Flatten
from sklearn.model_selection import train_test_split
from tensorflow.keras.utils import to_categorical
from tensorflow.data import AUTOTUNE
from tensorflow.keras.applications.vgg16 import VGG16
tf.config.run_functions_eagerly(True)

#* Default Kernel
DEFAULT_KERNEL_SIZE : Tuple[int, int]= (3,3)
DEFAULT_POOL_SIZE : Tuple[int, int]= (3,3)

#* Default Filter Size  
DEFAULT_FILTER_SIZE : int = 32
DEFAULT_FILTER_SIZE2 : int = 120

#* Default Dense units
DEFAULT_UNITS1 : int = 128
DEFAULT_UNITS2 : int = 64
DEFAULT_UNITS3 : int = 512

#* Default Dropout
DROPOUT : int = 0.3
class TumorModel(Model):
	def __init__(self, num_classes, **kwargs):
		super(TumorModel, self).__init__(**kwargs)
		self.vgg16 = VGG16(weights='imagenet', include_top=False)
		self.conv1 = Conv2D(filters=DEFAULT_FILTER_SIZE, kernel_size=DEFAULT_KERNEL_SIZE, activation='relu',name="conv1",padding="SAME")
		self.conv2 = Conv2D(filters=DEFAULT_FILTER_SIZE2, kernel_size=DEFAULT_KERNEL_SIZE, activation='relu',padding="SAME",name="conv2")
		self.conv3 = Conv2D(filters=DEFAULT_FILTER_SIZE2, kernel_size=DEFAULT_KERNEL_SIZE, activation='relu',padding="VALID",name="conv3")
		self.maxPooling1 = MaxPooling2D(pool_size=DEFAULT_POOL_SIZE)
		self.batch = BatchNormalization()
		self.flatten = Flatten()
		self.dense1 = Dense(units=DEFAULT_UNITS1, activation='relu')
		self.dense2 = Dense(units=DEFAULT_UNITS2, activation='relu')
		self.dense3 = Dense(units=DEFAULT_UNITS3, activation='relu')
		self.dropout= Dropout(DROPOUT)
		self.dropout2 = Dropout(DROPOUT+0.2)
		self.outputGan = Dense(units=num_classes, activation='softmax')

	def call(self, inputs, training= True):
		self.vgg16.trainable = False
		h = self.vgg16(inputs)
		h = self.conv1(h)
		h = self.flatten(h)
		h = self.dense1(h)
		h = self.dropout(h)
		h = self.dense3(h)
		h = self.dropout2(h)
		h = self.dense2(h)
		h = self.outputGan(h)
		return h

if __name__=="__main__":
	model = TumorModel(num_classes=3)
	model.compile(loss=tf.keras.losses.CategoricalCrossentropy(), optimizer=tf.keras.optimizers.Adam(0.001), metrics=['accuracy'])
	df = create_dataFrame(path_data="./data/brain_tumor/")
	df["label"] = df["label"].replace({"no" : 0,"yes" : 1,"random":2})
	print(df["label"].value_counts())
	model.build(input_shape=(32,224,224,3))
	model.summary()
	#df.to_csv("./data/brain_tumor/brain_tumor.csv")
	trainDataset, valDataset = convert_toTfDataset(df["image"].values, to_categorical(df["label"].values,num_classes=3))
	trainDataset = trainDataset.map(preprocessImage)
	valDataset = valDataset.map(preprocessImage)
	trainDataset = trainDataset.batch(batch_size=32).prefetch(AUTOTUNE)
	valDataset = valDataset.batch(batch_size=32).prefetch(AUTOTUNE)
	#model.fit(trainDataset, epochs=100, validation_data=valDataset)