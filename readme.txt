TrajectorySummarizer
お茶の水女子大学　伊藤研究室
2015年11月21日現在


■ プログラム構成
・srcフォルダ以下はJavaで書かれています。現状では以下の環境を前提としています。
　　JDK (Java Development Kit) 1.8.0
　　JOGL (Java binding OpenGL) 2.2.1
・Pythonフォルダ以下はPythonで書かれていて、Javaプログラムから呼び出されます。
　開発者はPythonのインストールにAnaconda3 (64ビット, Python3.4相当) を用いています。
　NumPy, SciPy, scikit-learn の各ライブラリのインストールが必要です。

■ 実行前の準備
・JDK, JOGL, Pythonの環境設定が必要です。
・Javaのプログラムのうちocha.itolab.hutch.core.tool.ClusteringInvokerにて、
　11,12行目に設定されたパスを書き換える必要があります。
・Javaのmainメソッドはocha.itolab.hutch.applet.pathviewer.ViewerMainにあります。

■ 操作手順
1) ocha.itolab.hutch.applet.pathviewer.ViewerMainを起動し、ウィンドウが
　表示されるのを確認して下さい。
2) JSON/CSV File Open ボタンを押してデータファイルを開いて下さい。
3) Clusteringボタンを押すことで、経路をクラスタリングした色分け結果を表示します。
　クラスタリングにはPythonのSpectral Clusteringが呼び出されています。
　・Num.cluster欄の数字を操作することでクラスタ数を調節できます。
　・Clustering ratioスライダを調節することで、左に動かすと軌跡の方向に重みをおいた結果、
　　右に動かすことで位置に重みをおいた結果を表示します。
4) Poplutationボタンを押すと通過人数、Stopnessボタンを押すと停止人数、
　Directionボタンを押すと通過方向を示します。
5) 4)で色のついた位置にてマウスをクリックすると、そこを通過する人の軌跡を表示します。
