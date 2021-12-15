#%%
from preprocessing import *
import tensorflow as tf
from tensorflow.keras.models import Model
from tensorflow.keras.layers import Dense, Input, Dropout, Conv2D, MaxPooling2D, BatchNormalization,Flatten
from sklearn.model_selection import train_test_split
from tensorflow.data import AUTOTUNE
from tensorflow.keras.utils import to_categorical
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

def createModel():
	model_input = Input(shape=(224,224,1))
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
if __name__=="__main__":
	model = createModel()
	model.compile(loss=tf.keras.losses.CategoricalCrossentropy(), optimizer=tf.keras.optimizers.Adam(), metrics=['accuracy'])
	df = create_dataFrame(path_data="./data/brain_tumor/")
	df["label"] = df["label"].replace({"no" : 0,"yes" : 1,"random":2})
	print(df["label"].value_counts())
	#df.to_csv("./data/brain_tumor/brain_tumor.csv")
	trainDataset, valDataset = convert_toTfDataset(df["image"].values, to_categorical(df["label"].values,num_classes=3))
	trainDataset = trainDataset.map(preprocessImage)
	valDataset = valDataset.map(preprocessImage)
	trainDataset = trainDataset.batch(batch_size=32).prefetch(AUTOTUNE)
	valDataset = valDataset.batch(batch_size=32).prefetch(AUTOTUNE)
	model.fit(trainDataset, epochs=10, validation_data=valDataset)