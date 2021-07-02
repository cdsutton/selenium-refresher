package com.revature.app;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.opencsv.CSVWriter;

import io.github.bonigarcia.wdm.WebDriverManager;

public class CodinGameAutomation {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		//User Input
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter CodinGame Email");
		String email = scanner.nextLine();
		
		//Testing Password Mask
//		Console console = System.console() ;
//		char [] mask = console.readPassword("Enter password: ");
//		Arrays.fill(mask,'*');
//		String password = new String(mask);
		System.out.println("Enter CodinGame Password");
		String password = scanner.nextLine();
		
		//User Input (Cont.)
		System.out.println("Enter URL");
		String url = scanner.nextLine();
		System.out.println("Enter new file name");
		String fileName = scanner.nextLine();
		
		//Setting up Selenium
		WebDriverManager.firefoxdriver().setup();
		FirefoxBinary firefoxBinary = new FirefoxBinary();
		firefoxBinary.addCommandLineOptions("--headless");
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary);
		FirefoxDriver driver = new FirefoxDriver(firefoxOptions);
		driver.get(url); //https://www.codingame.com/clashofcode/clash/report/182018842fdd90bc1942ab5ea8d5f7f994bbcf2

		WebDriverWait wdw = new WebDriverWait(driver, 20);
		
		//Cookies Tab
		Boolean acceptIsPresent = driver.findElements(By.xpath("//*[text()='Accept']")).size() > 0;

		wdw.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='Accept']")));
		if (acceptIsPresent)
			driver.findElement(By.xpath("//*[text()='Accept']")).click();

		
		//Login Tab
		wdw.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='Log In']")));
		driver.findElement(By.xpath("//*[text()='Log In']")).click();

		wdw.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name=\"email\"]")));
		driver.findElement(By.cssSelector("input[name=\"email\"]")).sendKeys(email);
		driver.findElement(By.cssSelector("input[name=\"password\"]")).sendKeys(password);
		driver.findElement(By.cssSelector("button[type=\"submit\"]")).click();

		//Automate Results
		Thread.sleep(2000);
		boolean viewCode = false;
		List<String[]> result = new ArrayList<String[]>();

		for (int i = 0; i < 100; i++) {
			int id = i + 1;
			if (driver.findElements(By.className("player-report")).get(i)
					.findElements(By.cssSelector("[ng-if=\"isSolutionVisible(player)\"]")).size() > 0) {
				viewCode = true;
			} else {
				viewCode = false;
			}

			String[] playerRecord = { String.valueOf(id),
					driver.findElements(By.className("nickname")).get(i).getText(),
					driver.findElements(By.xpath("//span[contains(text(), '%')]")).get(i).getText(),
					driver.findElements(By.xpath("//*[contains(text(), '00:')]")).get(i).getText(),
					String.valueOf(viewCode) };

			result.add(playerRecord);
		}

		//Convert to CSV
		String userHome = System.getProperty("user.home") + "/Desktop";
		File file = new File(userHome, fileName + ".csv");
		boolean fileResult = file.createNewFile();

		if (fileResult) {
			System.out.println("Created new file at " + file.getCanonicalPath());
		} else {
			System.out.println("File already exists at " + file.getCanonicalPath());
		}
		FileOutputStream fs = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fs);
		CSVWriter csvWriter = new CSVWriter(osw);

		csvWriter.writeAll(result);

		//Close Streams and Driver
		csvWriter.close();
		osw.close();
		fs.close();
		scanner.close();

		driver.quit();

	}

}
