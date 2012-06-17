package xuml.tools.jaxb;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import miuml.jaxb.Marshaller;

import org.apache.commons.io.IOUtils;

public class ModelServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String id = req.getParameter("id");
			String result = ModelPersistence.instance().getXml(id);
			resp.setContentType("text/plain");
			if (result != null)
				resp.getOutputStream().write(result.getBytes());
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String id = req.getParameter("id");
		String xml = req.getParameter("xml");
		// check can unmarshal
		try {
			new Marshaller().unmarshal(IOUtils.toInputStream(xml));
			// save
			ModelPersistence.instance().save(id, xml);
		} catch (RuntimeException e) {
			resp.setStatus(500);
			resp.getWriter().println(e.getMessage());
		}
	}

}
