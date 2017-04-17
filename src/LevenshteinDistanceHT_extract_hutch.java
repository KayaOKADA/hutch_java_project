//package (default package);

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class LevenshteinDistanceHT_extract_hutch {

	/*各コストの重み*/
	Double INSERT_WEIGHT = 1.0;//insertの重み
	Double DELETE_WEIGHT = 1.0;//deleteの重み
	Double CONVERT_WEIGHT = 1.0;//convertの重み

	/*操作している文字が、先頭・末尾・それ以外　のどれかを示すモード用定数*/
	static final int HEAD = 1;//記号列の先頭に記号を追加[(i,0)→(i,1)]、または先頭の文字を削除[(px.length,j)→(px.length+1,j)]
	static final int MIDDLE = 0;
	static final int TAIL = -1;//記号列の末尾に記号を追加、または末尾の文字を削除

	private String filename = "";// ラベルファイル名

	public LevenshteinDistanceHT_extract_hutch(String filename) {
		this.filename = filename;
		initRegionDistances();
	}

	/**
	 * region1とregion2間の距離
	 * @author miyagi
	 *
	 */
	public class RegionDistance {
		char region1;
		char region2;
		double distance;
	}

	/**
	 * RegionDistanceオブジェクトの生成
	 * @param region1
	 * @param region2
	 * @param distance
	 * @return
	 */
	public RegionDistance makeRegionDistance(char region1, char region2, double distance) {
		RegionDistance rd = new RegionDistance();
		rd.region1 = region1;
		rd.region2 = region2;
		rd.distance = distance;
		return rd;
	}

	ArrayList<Character> LabelList;//ラベルから読み込んだ領域名リスト
	ArrayList<RegionDistance> region_distances = new ArrayList<RegionDistance>();//ラベルから読み込んだ領域間の距離リスト

	/**
	 * region1とregion2の距離が保存されているか判定する
	 * @param region1
	 * @param region2
	 * @param region_distance
	 * @return
	 */
	public boolean hasDistance(char region1, char region2, RegionDistance region_distance) {
		boolean has = false;
		boolean b1 = (region1 == region_distance.region1) && (region2 == region_distance.region2);
		boolean b2 = (region1 == region_distance.region2) && (region2 == region_distance.region1);
		has = b1 || b2;
		return has;
	}

	/**
	 * region1とregion2の距離を返す（入力は順不同）
	 * @param region1
	 * @param region2
	 * @return
	 */
	public double getRegionDistance(char region1, char region2) {
		double distance = Double.MAX_VALUE;
		RegionDistance region_distance;
		boolean has_distance = false;

		if (region1 == region2) {
			return 0.0;
		}

		for (int i = 0; i < this.region_distances.size(); i++) {
			region_distance = this.region_distances.get(i);
			has_distance = hasDistance(region1, region2, region_distance);
			if (has_distance) {
				distance = region_distance.distance;
				break;
			}
		}
		//if (has_distance==false) {
		//	System.out.println("Distance between " + region1 + " and " + region2 + " is not saved.");
		//}
		return distance;
	}

	/**
	 * ラベルの情報を読み取り木に保存する
	 */
	private void initRegionDistances() {
		BufferedReader br;
		this.LabelList = new ArrayList<Character>();
		int label_number = 0;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line = "a";// 後で調整
			char state = 's';
			while ((line = br.readLine()) != null) {
				if (state == 's') {// 最初の読み飛ばし
					if (line.startsWith("# Regions")) {
						state = 'g';
					}
					continue;
				} else if (state == 'g') {// 文字
					if (line.equals("") || line.charAt(0) == ' ') {
						continue;
					}
					if (line.startsWith("# ")) {
						state = 'd';
						continue;
					}
					String[] region = line.split(",");//領域名、開始点ブロック番号、終了点ブロック番号に分割
					this.LabelList.add(region[0].charAt(0));//領域名のみリストに保存
				} else {// #Dictance 以下の距離の記録　（別関数にするかも）
					if (line.isEmpty()) {
						continue;
					}
					readOneLabelLine(line, label_number);
					label_number++;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 一行分の距離情報を記録する
	 * @param line
	 * @param label_number
	 */
	public void readOneLabelLine(String line, int label_number) {
		String[] distances = line.split(",");
		Double distance;
		for (int i = 0; i < distances.length; i++) {
			char region1 = LabelList.get(label_number);
			char region2 = LabelList.get(label_number + i + 1);
			distance = Double.valueOf(distances[i]);
			this.region_distances.add(makeRegionDistance(region1, region2, distance));
		}
	}

	/**
	 * 木に保存されている領域ペアと距離の一覧を出力する
	 */
	public void showRegionDistances() {
		for (RegionDistance rd : region_distances) {
			System.out.println(rd.region1 + ", " + rd.region2 + ", " + rd.distance);
		}
	}

	class costdata {
		double[][] row;// 通常のマトリクスの値()
		double[][][] allcosts;// 各セルでの挿入、削除、置換コスト
	}

	/**
	 * WLD計算用マトリクスの初期化
	 * @param len1
	 * @param len2
	 * @return
	 */
	public costdata initCostdata(String px, String py) {
		int px_length = px.length();
		int py_length = py.length();
		costdata cost_data = new costdata();
		cost_data.row = new double[px_length + 1][py_length + 1];
		cost_data.allcosts = new double[px_length + 1][py_length + 1][3];

		if (px_length < 1 || py_length < 1) {
			return cost_data;
		}

		cost_data.row[0][0] = 0.0;// スタート地点(0,0)は0

		for (int i = 1; i <= px_length - 1; i++) {//0行目の値をセット
			cost_data.row[i][0] = cost_data.row[i - 1][0] + calculateDeleteCost(px.charAt(i), px.charAt(i - 1), '\0', HEAD);
		}
		cost_data.row[px_length][0] = Double.MAX_VALUE;//右上のマスは文字数０なので通過させない

		cost_data.row[0][1] = calculateInsertCost(py.charAt(0), px.charAt(0), '\0', HEAD);//０列目の値をセット
		for (int i = 2; i <= py_length; i++) {
			cost_data.row[0][i] = cost_data.row[0][i - 1] + calculateInsertCost(py.charAt(i - 2), py.charAt(i - 1), px.charAt(0), MIDDLE);
		}
		return cost_data;
	}

	/**
	 * 文字列pxとpyの距離を返す
	 * 
	 * @param px
	 * @param py
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public double getWeightedLevenshteinDistance(String px, String py, double threshold) {
		if (px.length() == 1 && py.length() == 1) {
			return getRegionDistance(px.charAt(0), py.charAt(0));
		}
		costdata cost_data = initCostdata(px, py);

		//double distance = calculateAllBlockCost_short(px, py, cost_data, threshold);//
		double distance = calculateAllBlockCost_normal(px, py, cost_data);

		distance = correctDistance_by_length(px, py, distance);//2016/06/30 使うか検討中
		return distance;
	}

	/**
	 * 列ごとにWLDを計算
	 * @param px
	 * @param py
	 * @param c
	 * @return
	 */
	public double calculateAllBlockCost_normal(String px, String py, costdata c) {
		int len1 = px.length();
		int len2 = py.length();
		for (int i = 1; i <= len1; ++i) {// デフォルト 
			for (int j = 1; j <= len2; ++j) {//デフォルト 
				c = setOneBlockCost(px, py, i, j, c);
			}
		}
		double result = (Double) (c.row[len1][len2]);// 右下隅の値		  
		return result;
	}

	/**
	 * 距離閾値を超えたら計算を打ち切る方法でWLDを計算
	 * 右上隅から左下隅の対角線を→↓の順で走査させる
	 * 
	 * @param px
	 * @param py
	 * @param cost_data
	 * @return
	 */
	public double calculateAllBlockCost_short(String px, String py, costdata cost_data, double threshold) {
		int px_length = px.length();
		int py_length = py.length();
		int MAX_DIAGONAL_LENGTH = Math.min(px_length, py_length);// 対角線の長さ最大値
		int diagonal_length = 0;//対角線の長さ（1,2,... MAXLENGTH, ...2, 1)
		double min_wld = Double.MAX_VALUE;
		int x, y;//マスの座標
		int xy_sum = 2;// 対角線上にあるマスの、x,yの合計値（初期(1,1)で合計２）
		for (int i = 1; i < px_length; i++) {//対角線が開始する開始x座標を求める
			diagonal_length = Math.min(i, MAX_DIAGONAL_LENGTH);
			for (int j = i; (i - j) < diagonal_length; j--) {//処理するマスを決める
				x = j;
				y = xy_sum - x;
				cost_data = setOneBlockCost(px, py, x, y, cost_data);
				min_wld = Math.min(cost_data.row[x][y], min_wld);
			}
			if (isFar(threshold, min_wld) == true) {
				return min_wld;
			}
			xy_sum++;
		}
		for (int i = 1; i <= py_length; i++) {////対角線が開始する開始y座標を求める (右上隅のマスからスタート)
			diagonal_length = Math.min(py_length - i + 1, MAX_DIAGONAL_LENGTH);
			for (int j = i; (j - i) < diagonal_length; j++) {
				y = j;//default
				x = xy_sum - y;//default
				cost_data = setOneBlockCost(px, py, x, y, cost_data);
				min_wld = Math.min(cost_data.row[x][y], min_wld);
			}
			if (isFar(threshold, min_wld) == true) {
				return min_wld;
			}
			xy_sum++;
		}
		double result = (Double) (cost_data.row[px_length][py_length]);// 右下隅の値
		return result;
	}

	/**
	 * wldが閾値より大きい（=離れすぎてる）か判定する
	 * @param Threshold
	 * @param wld
	 * @return
	 */
	public boolean isFar(double Threshold, double wld) {
		boolean b = Threshold < wld;
		return b;
	}

	/**
	 * 指定したマスのコスト計算を行う
	 * @param px
	 * @param py
	 * @param i
	 * @param j
	 * @param cost_data
	 * @return
	 */
	public costdata setOneBlockCost(String px, String py, int i, int j, costdata cost_data) {
		double[] cost = calculate3kindsofCost(px, py, i, j);
		cost_data.allcosts[i][j] = cost;
		cost_data.row[i][j] = Math.min(
				Math.min((Double) (cost_data.row[i - 1][j - 1]) + ((px.substring(i - 1, i).equals(py.substring(j - 1, j))) ? 0 : cost[2]), // 1.replace
						(Double) (cost_data.row[i - 1][j]) + cost[1]), // 2.delete
				(Double) (cost_data.row[i][j - 1]) + cost[0]); // 3.insert
		return cost_data;
	}

	/**
	 * 指定したブロックに到達する際のすべてのパターンのコストを算出する
	 * @param px
	 * @param py
	 * @param i
	 * @param j
	 * @return
	 */
	public double[] calculate3kindsofCost(String px, String py, int i, int j) {
		double[] d = { 11, 23, 37 };// 挿入、削除、編集コスト

		if (i == px.length() && j == 1) {// 右上のセル
			//d[1] = getDistance(py.charAt(0), px.charAt(px.length() - 1));	//挿入なし
			d[1] = calculateDeleteCost('\0', py.charAt(0), px.charAt(px.length() - 1), TAIL);
			d[2] = calculateConvertCost(px.charAt(px.length() - 1), py.charAt(0));
		} else if (i == 1 && j == 1) {// 左上のセル　先頭の文字の比較
			//d[2] = getDistance(px.charAt(0), py.charAt(0));
			d[0] = calculateInsertCost(px.charAt(1), py.charAt(0), '\0', HEAD);
			d[1] = calculateDeleteCost(py.charAt(0), px.charAt(0), px.charAt(1), MIDDLE);
			d[2] = calculateConvertCost(px.charAt(0), py.charAt(0));
		} else if (i == px.length()) {// 右端の列
			d[0] = calculateInsertCost('\0', py.charAt(j - 1), py.charAt(j - 2), TAIL);
			d[1] = calculateDeleteCost('\0', px.charAt(px.length() - 1), py.charAt(j - 1), TAIL);
			d[2] = calculateConvertCost(py.charAt(j - 1), px.charAt(i - 1));
		} else if (i == 1) {// 左端の列
			d[0] = calculateInsertCost(py.charAt(j - 2), py.charAt(j - 1), px.charAt(1), MIDDLE);
			d[1] = calculateDeleteCost(py.charAt(j - 1), px.charAt(0), px.charAt(1), MIDDLE);
			d[2] = calculateConvertCost(py.charAt(j - 1), px.charAt(i - 1));
		} else if (j == 1) {// 上端の行
			d[0] = calculateInsertCost(px.charAt(i), py.charAt(0), '\0', HEAD);
			d[1] = calculateDeleteCost(py.charAt(0), px.charAt(i - 1), px.charAt(i), MIDDLE);
			d[2] = calculateConvertCost(py.charAt(j - 1), px.charAt(i - 1));
		} else {
			d[0] = calculateInsertCost(py.charAt(j - 2), py.charAt(j - 1), px.charAt(i), MIDDLE);
			d[1] = calculateDeleteCost(py.charAt(j - 1), px.charAt(i - 1), px.charAt(i), MIDDLE);
			d[2] = calculateConvertCost(py.charAt(j - 1), px.charAt(i - 1));
		}
		return d;
	}

	/**
	 * 挿入コストの計算
	 * @param departure
	 * @param add
	 * @param arrival
	 * @param pos
	 * @return
	 */
	public double calculateInsertCost(char departure, char add, char arrival, int pos) {
		double d = 0.0;
		if (pos == MIDDLE) {
			d = Math.abs(getRegionDistance(departure, add) + getRegionDistance(add, arrival) - getRegionDistance(departure, arrival));
		} else if (pos == HEAD) {
			d = getRegionDistance(add, departure);
		} else if (pos == TAIL) {
			d = getRegionDistance(arrival, add);
		}
		return d + INSERT_WEIGHT;
	}

	/**
	 * 削除コストの計算
	 * @param departure
	 * @param delete
	 * @param arrival
	 * @param pos
	 * @return
	 */
	public double calculateDeleteCost(char departure, char delete, char arrival, int pos) {
		double d = 0.0;
		if (pos == MIDDLE) {
			d = Math.abs(getRegionDistance(departure, delete) + getRegionDistance(delete, arrival) - getRegionDistance(departure, arrival));
		} else if (pos == HEAD) {
			d = getRegionDistance(delete, departure);
		} else if (pos == TAIL) {
			d = getRegionDistance(arrival, delete);
		}
		return d + DELETE_WEIGHT;
	}

	/**
	 * 編集コスト計算
	 * @param add
	 * @param delete
	 * @return
	 */
	public double calculateConvertCost(char add, char delete) {
		double d = 0.0;
		d = getRegionDistance(add, delete);
		return d * CONVERT_WEIGHT;
	}

	/**
	 * 比較する動線の平均文字数によって、距離を補正する
	 * （長い記号列同士の比較では距離が大きくなりやすいため）
	 * @param px
	 * @param py
	 * @param distance
	 * @return
	 */
	public double correctDistance_by_length(String px, String py, double distance) {
		double average_length = (px.length() + py.length()) / 2;
		double weight = 1.0;
		double coefficient = average_length * weight;
		distance = distance / coefficient;
		return distance;
	}

	/**********コンソール表示用関数色々***********/

	/**
	 * 指定したブロックにおける、文字の変化の過程を表示
	 * @param px
	 * @param py
	 * @param i
	 * @param j
	 * @return
	 */
	public String getProcessedLetters(String px, String py, int i, int j) {
		String str;
		String insert = "";
		String delete = "";
		String convert = "";
		if (i == 0 && j == 0) {// 左上のセル　先頭の文字の比較
			insert = "	";
			delete = "	";
			convert = "	";
		} else if (i == px.length() && j == 0) {// 右上のセル 削除と変換のみ
			insert = "	";
			delete = getOneLetterfromString(px, px.length() - 1) + "-" + getOneLetterfromString(px, px.length());
			convert = "	";
		} else if (i == 0) {// 左端の列　挿入のみ
			insert = getOneLetterfromString(py, j - 2) + getOneLetterfromString(px, 0) + "-" + getOneLetterfromString(py, j - 2)
					+ getOneLetterfromString(py, j - 1) + getOneLetterfromString(px, 0);
			delete = "	";
			convert = "	";
		} else if (j == 0) {// 上端の列　削除のみ
			insert = "	";
			delete = getOneLetterfromString(px, i - 1) + getOneLetterfromString(px, i) + "-" + getOneLetterfromString(px, i);
			convert = "	";
		} else {
			insert = getOneLetterfromString(py, j - 2) + getOneLetterfromString(px, i) + "-" + getOneLetterfromString(py, j - 2)
					+ getOneLetterfromString(py, j - 1) + getOneLetterfromString(px, i);
			delete = getOneLetterfromString(py, j - 1) + getOneLetterfromString(px, i - 1) + getOneLetterfromString(px, i) + "-"
					+ getOneLetterfromString(py, j - 1) + getOneLetterfromString(px, i);
			convert = getOneLetterfromString(px, i - 1) + "-" + getOneLetterfromString(py, j - 1);
		}
		str = "[" + insert + "," + delete + "," + convert + "]";
		return str;
	}

	/**
	 * strのi番目の文字をString型として返す
	 * @param str
	 * @param i
	 * @return
	 */
	public String getOneLetterfromString(String str, int i) {
		if (i < 0 || str.length() <= i) {
			return "*";//ダミー文字（領域名に使われてないもので）
		} else {
			return str.substring(i, i + 1);
		}
	}

	/**
	 *  文字列変換過程マトリクス内のセル(i, j)の文字列を返す
	 * @param px	変換前の文字列
	 * @param py	変換後の文字列
	 * @param i	列
	 * @param j	行
	 * @return
	 */
	public String getcurrentString(String px, String py, int i, int j) {
		String str = "";
		str = py.substring(0, j) + px.substring(i, px.length());
		return str;
	}

	/**
	 * 文字列の変換過程のマトリクスを表示
	 * 
	 * @param px
	 *            ...変換前の文字列
	 * @param py
	 *            ...変換後の文字列
	 */
	public void showStringMatrix(String px, String py) {
		for (int j = 1; j <= py.length(); j++) {
			for (int i = 1; i <= px.length(); i++) {
				System.out.print(getcurrentString(px, py, i, j) + '\t');
			}
			System.out.println();
		}
	}

	/**********テスト色々***********/

	/**
	 * ラベルデータの保存と読み取りテスト
	 */
	public static void testSaveLabelData() {
		String label_file = "LabelInfo_Test.txt";
		LevenshteinDistanceHT_extract_hutch ldh = new LevenshteinDistanceHT_extract_hutch(label_file);
		ldh.showRegionDistances();
		ldh.testGetDistances();
	}

	/**
	 * セル内の場合分けのテスト 簡易表示版 ok
	 * 
	 * @param px
	 * @param py
	 * @param i
	 * @param j
	 * @return
	 */
	public int getCostTest_num(String px, String py, int i, int j) {
		int n;
		if (i == 1 && j == 1) {// 左上のセル　先頭の文字の比較
			n = 1;
		} else if (i == px.length() && j == 1) {// 右上のセル 削除のみ
			n = 2;
		} else if (i == 1) {// 左端の列　挿入のみ
			n = 3;
		} else if (j == 1) {// 上端の列　削除のみ
			n = 4;
		} else if (i == px.length()) {// 右端の列
			n = 5;
		} else {
			n = 6;
		}
		return n;
	}

	/**
	 * セル内の場合分けのテスト 細かい版 ok
	 * 
	 * @param px
	 * @param py
	 * @param i
	 * @param j
	 * @return
	 */
	public String getCostTest_detail(String px, String py, int i, int j) {
		String str;
		if (i == 1 && j == 1) {// 左上のセル　先頭の文字の比較
			str = "[" + "*" + "," + "*" + "," + px.charAt(0) + py.charAt(0) + "]";
		} else if (i == px.length() && j == 1) {// 右上のセル 削除のみ
			str = "[" + "*" + "," + py.charAt(0) + px.charAt(px.length() - 1) + "," + "*" + "]";
		} else if (i == 1) {// 左端の列　挿入のみ
			str = "[" + py.charAt(j - 2) + px.charAt(1) + "-" + py.charAt(j - 2) + py.charAt(j - 1) + px.charAt(1) + "," + "*" + "," + "*" + "]";
		} else if (j == 1) {// 上端の列　削除のみ
			str = "[" + "*" + "," + py.charAt(0) + px.charAt(i - 1) + px.charAt(i) + "-" + py.charAt(0) + px.charAt(i) + "," + "*" + "]";
		} else if (i == px.length()) {// 右端の列
			str = "[" + py.charAt(j - 2) + py.charAt(j - 1) + "," + py.charAt(j - 1) + px.charAt(px.length() - 1) + "," + py.charAt(j - 2)
					+ px.charAt(px.length() - 1) + "-" + py.charAt(j - 2) + py.charAt(j - 1) + "]";
		} else {
			str = "[" + py.charAt(j - 2) + px.charAt(i) + "-" + py.charAt(j - 2) + py.charAt(j - 1) + px.charAt(i) + "," + py.charAt(j - 1)
					+ px.charAt(i - 1) + px.charAt(i) + "-" + py.charAt(j - 1) + px.charAt(i) + "," + py.charAt(j - 2) + px.charAt(i - 1)
					+ px.charAt(i) + "-" + py.charAt(j - 2) + py.charAt(j - 1) + px.charAt(i) + "]";
		}
		return str;
	}

	/**
	 * セル内の場合分けのテスト　結果表示 ok
	 * 
	 * @param px
	 * @param py
	 */
	public void showGetCostTest(String px, String py) {
		for (int j = 0; j <= py.length(); j++) {
			for (int i = 0; i <= px.length(); i++) {
				//System.out.print(getCostTest_num(px, py, i, j) + " ");//場合分けの番号表示
				System.out.print(getProcessedLetters(px, py, i, j) + " ");// 文字変化の詳細表示
			}
			System.out.println();
		}
	}

	/**
	 * 簡単な距離計算テスト
	 */
	public void testShortSample() {
		String[] keywords = { "ABCD", "ABC", "AB", "A" };
		String[] routes = { "EFG", "EF", "E", "DBCA", "DBC", "BCA", "CA" };
		for (String keyword : keywords) {
			for (String route : routes) {
				System.out.println(keyword + ":" + route + "	" + getWeightedLevenshteinDistance(keyword, route, Double.MAX_VALUE));
			}
			System.out.println();
		}
	}

	/**
	 * WLDの距離テスト（記号列を複数指定するバージョン）
	 * @param keywords
	 * @param routes
	 * @param times
	 */
	public void testWLD(String keywords[], String[] routes, int times) {
		int count = 0;
		for (String keyword : keywords) {
			for (String route : routes) {
				System.out.println(keyword + ":" + route + "	" + getWeightedLevenshteinDistance(keyword, route, Double.MAX_VALUE));
				count++;
				if (times < count) {
					count = 0;
					break;
				}
			}
			System.out.println();
		}
	}

	/**
	 * 距離情報検索テスト　OK
	 */
	public void testGetDistances() {
		System.out.println(getRegionDistance('a', 'c'));//left<right
		System.out.println(getRegionDistance('f', 'a'));//left>right
		System.out.println();
		System.out.println(getRegionDistance('d', 'g'));//same pair
		System.out.println(getRegionDistance('g', 'd'));
		System.out.println();
		System.out.println(getRegionDistance('A', 'X'));//not included
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String label_file = "LabelInfo_2_5_44_.txt";//領域と距離を記録したテキストファイル
		LevenshteinDistanceHT_extract_hutch ld = new LevenshteinDistanceHT_extract_hutch(label_file);

		ld.showRegionDistances();

		String px = "a";
		String py = "b";
		System.out.println();
		System.out.println("compare " + px + "," + py);

		System.out.println();
		ld.showStringMatrix(px, py);
		System.out.println();
		ld.showGetCostTest(px, py);
		System.out.println();
		System.out.println("distance=" + ld.getWeightedLevenshteinDistance(px, py, Double.MAX_VALUE));

	}
}